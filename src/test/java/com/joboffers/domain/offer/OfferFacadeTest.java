package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.OfferRequestDto;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

class OfferFacadeTest {
    @Test
    public void should_fetch_offers_and_save_them_all_when_repository_is_empty() {
        //when
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        //given
        List<OfferResponseDto> result = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        //then
        assertThat(result).hasSize(4);
    }

    @Test
    public void should_find_offer_by_id_when_offer_existed() {

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