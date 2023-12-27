package com.joboffers.infrastructure.offer.http.nofluffjobsclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DraftForFilteringJobOfferResponseDto(
        String name,
        String title,
        List<String> seniority,
        String url,
        Salary salary
) {
}
