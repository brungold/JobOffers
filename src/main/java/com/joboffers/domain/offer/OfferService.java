package com.joboffers.domain.offer;

import com.joboffers.domain.offer.error.OfferDuplicateException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class OfferService {
    private final OfferFetchable offerFetchable;
    private final OfferRepository offerRepository;

    List<Offer> fetchAllOffersAndSaveAllIfNotExists() {
        List<Offer> offers = fetchAllOffers();
//        final List<Offer> jobOffers = findNotExistingOffers(offers);
        try {
            return offers;
//            return offerRepository.saveAll(jobOffers);
        } catch (OfferDuplicateException duplicateException) {
            throw new OfferDuplicateException(duplicateException.getMessage() + offers);
        }
    }

    private List<Offer> findNotExistingOffers(List<Offer> offers) {
        return offers.stream()
                .filter(offerDto -> !offerDto.offerUrl().isEmpty())
                .filter(offerDto -> !offerRepository.existsByUrl(offerDto.offerUrl()))
                .collect(Collectors.toList());
    }

    private List<Offer> fetchAllOffers() {
        return offerFetchable.fetchAllOffers()
                .stream()
                .map(OfferMapper::mapFromJobOfferResponseToOffer)
                .toList();
    }
}
