package com.joboffers.domain.offer.dto;

import lombok.Builder;

@Builder
public record JobOfferResponse(
        String company,
        String station,
        String wages,
        String offerUrl
) {
}
