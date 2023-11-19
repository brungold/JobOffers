package com.joboffers.infrastructure.offer.http.nofluffjobsproxy.dto;

import java.util.List;

public record DraftListForFilteringJobOfferResponseDto(List<DraftForFilteringJobOfferResponseDto> postings) {
}
