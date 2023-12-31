package com.joboffers.domain.offer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OfferFacadeConfiguration {

    @Bean
    OfferFacade offerFacade(@Qualifier("remoteOfferClientPracujPl") OfferFetchable pracujPlOfferClient,
                            @Qualifier("remoteOfferClient") OfferFetchable noFluffJobsOfferClient,
                            OfferRepository repository) {
        OfferService offerService = new OfferService(noFluffJobsOfferClient, pracujPlOfferClient, repository);
        return new OfferFacade(repository, offerService);
    }
}
