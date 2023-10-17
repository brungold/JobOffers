package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.OfferRequestDto;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    }

    @Test
    public void should_throw_not_found_exception_when_offer_not_found() {

    }

    @Test
    public void should_throw_duplicate_exception_when_offer_url_exists() {

    }
}