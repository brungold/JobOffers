package com.joboffers.infrastructure.offer.http.pracujplscrapperclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.joboffers.domain.offer.dto.JobOfferResponse;

import java.util.ArrayList;
import java.util.List;
public class PracujPlMapper {
    public static List<JobOfferResponse> mapFromJobOfferNodesToListJobOfferResponse(JsonNode jobOffersNode) {
        List<JobOfferResponse> jobOffers = new ArrayList<>();

        for (JsonNode jobOfferNode : jobOffersNode) {
            String jobTitle = jobOfferNode.at("/jobTitle").asText();
            String companyName = jobOfferNode.at("/companyName").asText();
            String salaryDisplayText = jobOfferNode.at("/salaryDisplayText").asText();
            String offerAbsoluteUri = jobOfferNode.at("/offers/0/offerAbsoluteUri").asText();

            JobOfferResponse jobOfferResponse = new JobOfferResponse(jobTitle, companyName, salaryDisplayText, offerAbsoluteUri);
            jobOffers.add(jobOfferResponse);
        }

        return jobOffers;
    }
}
