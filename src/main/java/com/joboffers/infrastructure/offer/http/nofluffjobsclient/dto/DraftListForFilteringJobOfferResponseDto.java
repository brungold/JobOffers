package com.joboffers.infrastructure.offer.http.nofluffjobsclient.dto;

import java.util.List;

public record DraftListForFilteringJobOfferResponseDto(List<DraftForFilteringJobOfferResponseDto> postings) {
}
