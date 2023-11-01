package com.joboffers.domain.offer.error;

import lombok.Getter;

@Getter
public class OfferNotFoundException extends RuntimeException {

    public OfferNotFoundException(String id) {
        super(String.format("Offer with id %s not found", id));
    }
}
