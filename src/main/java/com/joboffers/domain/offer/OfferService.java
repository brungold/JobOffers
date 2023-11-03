package com.joboffers.domain.offer;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class OfferService {
    private final OfferFetchable offerFetchable;
    private final OfferRepository offerRepository;

    List<Offer> fetchAllOffersAndSaveAllIfNotExists() {
        List<Offer> offers = fetchAllOffers();
        final List<Offer> jobOffers = findNotExistingOffers(offers);
        return offerRepository.saveAll(jobOffers);
    }

    private List<Offer> fetchAllOffers() {
        return offerFetchable.fetchAllOffers()
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
