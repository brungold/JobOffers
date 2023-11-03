package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.JobOfferResponse;
import com.joboffers.domain.offer.dto.OfferRequestDto;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import com.joboffers.domain.offer.error.OfferNotFoundException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;

class OfferFacadeTest {
    @Test
    public void should_fetch_offers_and_save_them_all_when_repository_is_empty() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        //when
        List<OfferResponseDto> result = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        //then
        assertThat(result).hasSize(4);
    }

    @Test
    public void should_find_offer_by_id_when_offer_existed() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.saveOffer(new OfferRequestDto("Google", "Junior", "6000", "www.someurl1.com/1"));

        //when
        OfferResponseDto offerFindById = offerFacade.findOfferById(offerResponseDto.id());

        //then
        assertThat(offerFindById).isEqualTo(OfferResponseDto.builder()
                .id(offerFindById.id())
                .companyName("Google")
                .position("Junior")
                .salary("6000")
                .offerUrl("www.someurl1.com/1")
                .build()
        );
    }

    @Test
    public void should_save_only_1_offer_when_repository_had_4_added_with_offer_urls() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();
        offerFacade.saveOffer(new OfferRequestDto("Google", "Junior", "6000", "www.someurl99.com/99"));
        offerFacade.saveOffer(new OfferRequestDto("Asseco", "Junior", "5000", "www.oferta1.com/1"));
        offerFacade.saveOffer(new OfferRequestDto("Intel", "Junior", "6000", "www.oferta2.com/2"));
        offerFacade.saveOffer(new OfferRequestDto("Microsoft", "Junior", "5500", "www.oferta3.com/3"));
        assertThat(offerFacade.findAllOffers()).hasSize(4);

        //when
        List<OfferResponseDto> result = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).offerUrl()).isEqualTo("www.oferta4.com/4");
    }

    @Test
    public void should_throw_not_found_exception_when_offer_not_found() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        //when
        Throwable thrown = catchThrowable(() -> offerFacade.findOfferById("99"));

        //
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(OfferNotFoundException.class)
                .hasMessage("Offer with id 99 not found");
    }

    @Test
    public void should_throw_duplicate_exception_when_offer_url_exists() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.saveOffer(new OfferRequestDto("Avast", "junior", "5500", "www.oferta100.com/100"));
        String offerId = offerResponseDto.id();
        assertThat(offerFacade.findOfferById(offerId).id()).isEqualTo(offerId);

        //when
        Throwable thrown = catchThrowable(() -> offerFacade.saveOffer(
                new OfferRequestDto("unknown", "junior", "unknown", "www.oferta100.com/100")
        ));

        //then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("Offer with url www.oferta100.com/100 already exists");
    }

    @Test
    public void should_throw_2_duplicate_exception_when_offer_url_exists() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();

        OfferResponseDto offerResponseDto1 = offerFacade.saveOffer(new OfferRequestDto("unknow", "Junior", "5500", "www.oferta3.com/3"));
        String offerId1 = offerResponseDto1.id();
        assertThat(offerFacade.findOfferById(offerId1).id()).isEqualTo(offerId1);

        OfferResponseDto offerResponseDto2 = offerFacade.saveOffer(new OfferRequestDto("unknow", "Junior", "unknow", "www.oferta4.com/4"));
        String offerId2 = offerResponseDto2.id();
        assertThat(offerFacade.findOfferById(offerId2).id()).isEqualTo(offerId2);

        //when
        Throwable thrown1 = catchThrowable(() -> offerFacade.saveOffer(
                new OfferRequestDto("Amazon", "junior", "unknown", "www.oferta3.com/3")
        ));

        Throwable thrown2 = catchThrowable(() -> offerFacade.saveOffer(
                new OfferRequestDto("unknow", "junior/mid", "unknown", "www.oferta4.com/4")
        ));

        //then
        AssertionsForClassTypes.assertThat(thrown1)
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("Offer with url www.oferta3.com/3 already exists");

        AssertionsForClassTypes.assertThat(thrown2)
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("Offer with url www.oferta4.com/4 already exists");
    }

    @Test
    public void should_return_empty_list_when_no_offers_exist() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();

        //when
        List<OfferResponseDto> result = offerFacade.findAllOffers();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    public void should_return_expected_offers() {
        //given
        List<JobOfferResponse> jobOffers = List.of(
                new JobOfferResponse("Junior", "Unknow", "5000", "www.url1.com"),
                new JobOfferResponse("Senior", "Amazon", "8000", "www.url2.com")
        );
        InMemoryOfferFetchable offerFetchable = new InMemoryOfferFetchable(jobOffers);

        //when
        List<JobOfferResponse> fetchedOffers = offerFetchable.fetchAllOffers();

        //then
        assertThat(fetchedOffers).containsExactlyElementsOf(jobOffers);
    }
}