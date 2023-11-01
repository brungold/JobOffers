package com.joboffers.domain.offer.error;

import lombok.Getter;

import java.util.List;

@Getter
public class OfferDuplicateException extends RuntimeException {
    private final List<String> offersUrls;

    public OfferDuplicateException(String url) {
        super(String.format("Offer with url %s already exists", url));
        this.offersUrls = List.of(url);
    }
}
