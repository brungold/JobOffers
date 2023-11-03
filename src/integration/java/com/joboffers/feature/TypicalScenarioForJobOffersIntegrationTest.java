package com.joboffers.feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.joboffers.BaseIntegrationTest;
import com.joboffers.SampleJobOfferResponse;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import com.joboffers.infrastructure.offer.scheduler.OfferHttpScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TypicalScenarioForJobOffersIntegrationTest extends BaseIntegrationTest implements SampleJobOfferResponse {

    @Autowired
    OfferHttpScheduler offerHttpScheduler;

    @Test
    public void should_go_through_the_job_offers_application() throws Exception {
        // step 1: there are no offers in external HTTP server
        // (http://ec2-3-120-147-150.eu-central-1.compute.amazonaws.com:5057/offers)
        // given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithZeroOffersJson())));


        // step 2: scheduler ran 1st time and made GET to external server and system added 0 offers to database
        // given & when
        List<OfferResponseDto> newOffers = offerHttpScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(newOffers).hasSize(0);


        // step 3: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned UNAUTHORIZED(401)
        // given
        // when
        // then
        // step 4: user made GET /offers with no jwt token and system returned UNAUTHORIZED(401)
        // step 5: user made POST /register with username=someUser, password=somePassword and system registered user with status OK(200)
        // step 6: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC


        // step 7: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 0 offers
        // given
        String offerUrl = "/offers";
        // when
        ResultActions perform = mockMvc.perform(get(offerUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        // then
        MvcResult mvcResult2 = perform.andExpect(status().isOk()).andReturn();
        String jsonWithOffers = mvcResult2.getResponse().getContentAsString();
        List<OfferResponseDto> offers = objectMapper.readValue(jsonWithOffers, new TypeReference<>() {
        });
        assertThat(offers).isEmpty();

        // step 8: there are 2 new offers in external HTTP server
        // given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithTwoOffersJson())));


        // step 9: scheduler ran 2nd time and made GET to external server and system added 2 new offers with ids: 1000 and 2000 to database
        // given && when
        List<OfferResponseDto> responseTwoNewOffers = offerHttpScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(responseTwoNewOffers).hasSize(2);


        // step 10: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 2 offers with ids: 1000 and 2000
        // given && when
        ResultActions performGetTwoOffers = mockMvc.perform(get(offerUrl)
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
        ResultActions performGetOffersWithNotExistingId = mockMvc.perform(get("/offers/" + notExistingId));
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
        // step 13: there are 2 new offers in external HTTP server
        // step 14: scheduler ran 3rd time and made GET to external server and system added 2 new offers with ids: 3000 and 4000 to database
        // step 15: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 4 offers with ids: 1000,2000, 3000 and 4000
        //step 16: user made POST /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and offer as body and system returned CREATED(201) with saved offer
        // given && when
        ResultActions performPostOffersWithOneOffer = mockMvc.perform(post("/offers")
                .content("""
                        {
                        "companyName": "someCompany",
                        "position": "somePosition",
                        "salary": "7 000 - 9 000 PLN",
                        "offerUrl": "https://newoffers.pl/offer/1234"
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
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
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        String oneOfferJson = peformGetOffers.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> parsedJsonWithOneOffer = objectMapper.readValue(oneOfferJson, new TypeReference<>() {
        });
        assertThat(parsedJsonWithOneOffer).hasSize(3);
        assertThat(parsedJsonWithOneOffer.stream().map(OfferResponseDto::id)).contains(id);
    }
}
