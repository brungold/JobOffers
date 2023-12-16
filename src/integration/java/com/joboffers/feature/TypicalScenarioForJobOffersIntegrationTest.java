package com.joboffers.feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.joboffers.BaseIntegrationTest;
import com.joboffers.SampleJobOfferResponse;
import com.joboffers.domain.loginandregisterfacade.dto.RegistrationResultDto;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import com.joboffers.infrastructure.loginandregister.controller.dto.JwtResponseDto;
import com.joboffers.infrastructure.offer.scheduler.OfferHttpScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TypicalScenarioForJobOffersIntegrationTest extends BaseIntegrationTest implements SampleJobOfferResponse {


    @Autowired
    OfferHttpScheduler offerHttpScheduler;
    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("offer.http.client.config.uri", () -> WIRE_MOCK_HOST);
        registry.add("offer.http.client.config.port", () -> wireMockServer.getPort());
    }

    @Test
    public void should_go_through_the_job_offers_application() throws Exception {
        // step 1: there are no offers in external HTTP server
        // (http://ec2-3-120-147-150.eu-central-1.compute.amazonaws.com:5057/offers)
        //  GET https://nofluffjobs.com/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl
        //    GET https://nofluffjobs.com:433/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl
        // given && when && then
        wireMockServer.stubFor(WireMock.get("/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody(bodyWithZeroOffersJson())));


        // step 2: scheduler ran 1st time and made GET to external server and system added 0 offers to database
        // given & when
        List<OfferResponseDto> newOffers = offerHttpScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(newOffers).hasSize(0);


        // step 3: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned UNAUTHORIZED(401)
        // given & when
        ResultActions failedLoginRequest = mockMvc.perform(post("/token")
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        failedLoginRequest
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("""
                        {
                          "message": "Bad Credentials",
                          "status": "UNAUTHORIZED"
                        }
                        """.trim()));


        // step 4: user made GET /offers with no jwt token and system returned UNAUTHORIZED(401)
        // given & when
        ResultActions failedGetOffersRequest = mockMvc.perform(get("/offers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        failedGetOffersRequest.andExpect(status().isForbidden());


        // step 5: user made POST /register with username=someUser, password=somePassword and system registered user with status CREATED(201)
        // given & when
        ResultActions registerAction = mockMvc.perform(post("/register")
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        MvcResult registerActionResult = registerAction.andExpect(status().isCreated()).andReturn();
        String registerActionResultJson = registerActionResult.getResponse().getContentAsString();
        RegistrationResultDto registrationResultDto = objectMapper.readValue(registerActionResultJson, RegistrationResultDto.class);
        assertAll(
                () -> assertThat(registrationResultDto.username()).isEqualTo("someUser"),
                () -> assertThat(registrationResultDto.created()).isTrue(),
                () -> assertThat(registrationResultDto.id()).isNotNull()
        );


        // step 6: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC
        // given & when
        ResultActions successLoginRequest = mockMvc.perform(post("/token")
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        MvcResult mvcResult = successLoginRequest.andExpect(status().isOk()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        JwtResponseDto jwtResponseDto = objectMapper.readValue(json, JwtResponseDto.class);
        String token = jwtResponseDto.token();
        assertAll(
                () -> assertThat(jwtResponseDto.username()).isEqualTo("someUser"),
                () -> assertThat(token).matches(Pattern.compile("^([A-Za-z0-9-_=]+\\.)+([A-Za-z0-9-_=])+\\.?$"))
        );


        // step 7: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 0 offers
        // given
        String offerUrl = "/offers";
        // when
        ResultActions perform = mockMvc.perform(get(offerUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        // then
        MvcResult mvcResult2 = perform.andExpect(status().isOk()).andReturn();
        String jsonWithOffers = mvcResult2.getResponse().getContentAsString();
        List<OfferResponseDto> offers = objectMapper.readValue(jsonWithOffers, new TypeReference<>() {
        });
        assertThat(offers).isEmpty();

        // step 8: there are 2 new offers in external HTTP server
        // given && when && then
        //https: //nofluffjobs.com:433/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl
        //http://localhost:51805/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl
        wireMockServer.stubFor(WireMock.get("/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody(bodyWithTwoOffersJson())));


        // step 9: scheduler ran 2nd time and made GET to external server and system added 2 new offers with ids: 1000 and 2000 to database
        // given && when
        List<OfferResponseDto> responseTwoNewOffers = offerHttpScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(responseTwoNewOffers).hasSize(2);


        // step 10: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 2 offers with ids: 1000 and 2000
        // given && when
        ResultActions performGetTwoOffers = mockMvc.perform(get(offerUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        // then
        MvcResult performGetTwoOffersMvcResult = performGetTwoOffers.andExpect(status().isOk()).andReturn();
        String jsonWithTwoOffers = performGetTwoOffersMvcResult.getResponse().getContentAsString();
        List<OfferResponseDto> twoOffers = objectMapper.readValue(jsonWithTwoOffers, new TypeReference<>() {
        });
        assertThat(twoOffers).hasSize(2);
        OfferResponseDto expectedOfferNoOne = twoOffers.get(0);
        OfferResponseDto expectedOfferNoTwo = twoOffers.get(1);
        assertThat(twoOffers).containsExactlyInAnyOrder(
                new OfferResponseDto(expectedOfferNoOne.id(), expectedOfferNoOne.companyName(), expectedOfferNoOne.position(), expectedOfferNoOne.salary(), expectedOfferNoOne.offerUrl()),
                new OfferResponseDto(expectedOfferNoTwo.id(), expectedOfferNoTwo.companyName(), expectedOfferNoTwo.position(), expectedOfferNoTwo.salary(), expectedOfferNoTwo.offerUrl())
        );


        // step 11: user made GET /offers/9999 and system returned NOT_FOUND(404) with message “Offer with id 9999 not found”
        // given
        String notExistingId = "9999";
        // when
        ResultActions performGetOffersWithNotExistingId = mockMvc.perform(get("/offers/" + notExistingId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        // then
        performGetOffersWithNotExistingId.andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                        "message": "Offer with id 9999 not found",
                        "status": "NOT_FOUND"
                        }
                        """.trim()
                ));

        // step 12: user made GET /offers/1000 and system returned OK(200) with offer
        // given
        String offerId = expectedOfferNoOne.id();
        // when
        ResultActions getOfferById = mockMvc.perform(get("/offers/" + offerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        String jsonExpectedOfferNoOne = getOfferById.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        OfferResponseDto offerNoOneById = objectMapper.readValue(jsonExpectedOfferNoOne, OfferResponseDto.class);
        assertThat(offerNoOneById).isEqualTo(expectedOfferNoOne);

        // step 13: there are 2 new offers in external HTTP server
        // given && when && then
        wireMockServer.stubFor(WireMock.get("/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody(bodyWithFourOffersJson())));


        // step 14: scheduler ran 3rd time and made GET to external server and system added 2 new offers with ids: 3000 and 4000 to database
        // given && when
        List<OfferResponseDto> twoNewOffers = offerHttpScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(twoNewOffers).hasSize(2);


        // step 15: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 4 offers with ids: 1000,2000, 3000 and 4000
        // given && when
        ResultActions performGetFourOffers = mockMvc.perform(get(offerUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        // then
        MvcResult performGetFourOffersMvcResult = performGetFourOffers.andExpect(status().isOk()).andReturn();
        String jsonWithFourOffers = performGetFourOffersMvcResult.getResponse().getContentAsString();
        List<OfferResponseDto> fourOffers = objectMapper.readValue(jsonWithFourOffers, new TypeReference<>() {
        });
        assertThat(fourOffers).hasSize(4);
        OfferResponseDto expectedOfferNoThree = twoNewOffers.get(0);
        OfferResponseDto expectedOfferNoFour = twoNewOffers.get(1);
        assertThat(fourOffers).contains(
                new OfferResponseDto(expectedOfferNoThree.id(), expectedOfferNoThree.companyName(), expectedOfferNoThree.position(), expectedOfferNoThree.salary(), expectedOfferNoThree.offerUrl()),
                new OfferResponseDto(expectedOfferNoFour.id(), expectedOfferNoFour.companyName(), expectedOfferNoFour.position(), expectedOfferNoFour.salary(), expectedOfferNoFour.offerUrl())
        );


        //step 16: user made POST /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and offer as body and system returned CREATED(201) with saved offer
        // given && when
        ResultActions performPostOffersWithOneOffer = mockMvc.perform(post("/offers")
                .header("Authorization", "Bearer " + token)
                .content("""
                        {
                        "companyName": "someCompany",
                        "position": "somePosition",
                        "salary": "7 000 - 9 000 PLN",
                        "offerUrl": "https://newoffers.pl/offer/1234"
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        String createdOfferJson = performPostOffersWithOneOffer.andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OfferResponseDto parsedCreatedOfferJson = objectMapper.readValue(createdOfferJson, OfferResponseDto.class);
        String id = parsedCreatedOfferJson.id();
        assertAll(
                () -> assertThat(parsedCreatedOfferJson.offerUrl()).isEqualTo("https://newoffers.pl/offer/1234"),
                () -> assertThat(parsedCreatedOfferJson.companyName()).isEqualTo("someCompany"),
                () -> assertThat(parsedCreatedOfferJson.salary()).isEqualTo("7 000 - 9 000 PLN"),
                () -> assertThat(parsedCreatedOfferJson.position()).isEqualTo("somePosition"),
                () -> assertThat(id).isNotNull()
        );


        //step 17: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 1 offer
        // given & when
        ResultActions peformGetOffers = mockMvc.perform(get("/offers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        String oneOfferJson = peformGetOffers.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> parsedJsonWithOneOffer = objectMapper.readValue(oneOfferJson, new TypeReference<>() {
        });
        assertThat(parsedJsonWithOneOffer).hasSize(5);
        assertThat(parsedJsonWithOneOffer.stream().map(OfferResponseDto::id)).contains(id);
    }
}
