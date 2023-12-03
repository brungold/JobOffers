package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.JobOfferResponse;

import java.util.List;

public class OfferFacadeTestConfiguration {

    private final InMemoryOfferFetchable offerFetchable;
    private final InMemoryOfferRepository offerRepository;

    OfferFacadeTestConfiguration() {
        this.offerFetchable = new InMemoryOfferFetchable(
                List.of(
                        new JobOfferResponse("Junior","Asseco" , "5000", "www.oferta1.com/1"),
                        new JobOfferResponse("Junior", "Intel", "6000", "www.oferta2.com/2"),
                        new JobOfferResponse("Junior", "Microsoft", "5500", "www.oferta3.com/3"),
                        new JobOfferResponse("Junior", "Comarch", "3000", "www.oferta4.com/4")
                )
        );
        this.offerRepository = new InMemoryOfferRepository();
    }

    OfferFacadeTestConfiguration(List<JobOfferResponse> clientOffers) {
        this.offerFetchable = new InMemoryOfferFetchable(clientOffers);
        this.offerRepository = new InMemoryOfferRepository();
    }

    OfferFacade offerFacadeForTests() {
        return new OfferFacade(offerRepository, new OfferService(offerFetchable, offerRepository));
    }
}


