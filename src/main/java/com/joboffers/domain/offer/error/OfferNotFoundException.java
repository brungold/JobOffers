package com.joboffers.domain.offer.error;

import lombok.Getter;

@Getter
public class OfferNotFoundException extends RuntimeException {
    private final String id;

    public OfferNotFoundException(String id) {
        super(String.format("Offer with id %s not found", id));
        this.id = id;

    }
}
