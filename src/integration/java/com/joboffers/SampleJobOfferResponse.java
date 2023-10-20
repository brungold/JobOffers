package com.joboffers;

public interface SampleJobOfferResponse {
    default String bodyWithZeroOffersJson() {
        return "[]";
    }
}
