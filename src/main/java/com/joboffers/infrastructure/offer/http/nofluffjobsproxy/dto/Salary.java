package com.joboffers.infrastructure.offer.http.nofluffjobsproxy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Salary(Integer from, Integer to, String currency) {
}
