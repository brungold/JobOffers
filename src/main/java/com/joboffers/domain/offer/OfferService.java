package com.joboffers.domain.offer;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class OfferService {
    @Qualifier("pracujPlOfferClient")
    private final OfferFetchable pracujPlOfferClient;
    @Qualifier("noFluffJobsOfferClient")
    private final OfferFetchable noFluffJobsOfferClient;
    private final OfferRepository offerRepository;

//    public OfferService(
//            @Qualifier("pracujPlOfferClient") OfferFetchable pracujPlOfferClient,
//            @Qualifier("noFluffJobsOfferClient") OfferFetchable noFluffJobsOfferClient,
//            OfferRepository offerRepository) {
//        this.pracujPlOfferClient = pracujPlOfferClient;
//        this.noFluffJobsOfferClient = noFluffJobsOfferClient;
//        this.offerRepository = offerRepository;
//    }

    List<Offer> fetchAllOffersAndSaveAllIfNotExists() {
        final List<Offer> allOffers = fetchAllJobOfferResponses();
        final List<Offer> jobOffers = findNotExistingOffers(allOffers);
        return offerRepository.saveAll(jobOffers);
    }

    private List<Offer> fetchAllJobOfferResponses() {
        List<Offer> pracujPlOffers = fetchAllOffersFromPracujPl();
        List<Offer> noFluffJobsOffers = fetchAllOffers();
        return Stream.concat(noFluffJobsOffers.stream(), pracujPlOffers.stream())
                .collect(Collectors.toList());
    }

    private List<Offer> fetchAllOffersFromPracujPl() {
        return pracujPlOfferClient.fetchAllOffers()
                .stream()
                .map(OfferMapper::mapFromJobOfferResponseToOffer)
                .toList();
    }

    private List<Offer> fetchAllOffers() {
        return noFluffJobsOfferClient.fetchAllOffers()
                .stream()
                .map(OfferMapper::mapFromJobOfferResponseToOffer)
                .toList();
    }

    private List<Offer> findNotExistingOffers(List<Offer> offers) {
        return offers.stream()
                .filter(offerDto -> !offerDto.offerUrl().isEmpty())
                .filter(offerDto -> !offerRepository.existsByOfferUrl(offerDto.offerUrl()))
                .collect(Collectors.toList());
    }
}
