package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.JobOfferResponse;

import java.util.List;

public class InMemoryOfferFetchable implements OfferFetchable {

    List<JobOfferResponse> offersList;

    public InMemoryOfferFetchable(List<JobOfferResponse> offersList) {
        this.offersList = offersList;
    }

    @Override
    public List<JobOfferResponse> fetchAllOffers() {
        return offersList;
    }
}
