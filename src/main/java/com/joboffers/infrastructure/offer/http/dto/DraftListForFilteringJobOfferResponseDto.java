package com.joboffers.infrastructure.offer.http.dto;

import java.util.List;

public record DraftListForFilteringJobOfferResponseDto(List<DraftForFilteringJobOfferResponseDto> postings) {
}
