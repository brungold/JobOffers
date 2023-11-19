package com.joboffers.infrastructure.offer.http.pracujplscrapper;

import com.joboffers.domain.offer.dto.JobOfferResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PracujPlService {

    public static List<JobOfferResponse> filterBySeniorityTechnologyAndPlaceInUrl(List<JobOfferResponse> jobOffers) {
        return jobOffers.stream()
                .filter(jobOfferResponse -> filterByTitleSeniority(jobOfferResponse))
                .filter(jobOfferResponse -> filterByTitleTechnology(jobOfferResponse))
                .filter(jobOfferResponse -> filterByUrlPlace(jobOfferResponse))
                .collect(Collectors.toList());
    }

    private static boolean filterByTitleSeniority(JobOfferResponse offer) {
        String title = offer.title().toLowerCase();
        return title.contains("junior") || title.contains("młody") || title.contains("młodszy");
    }

    private static boolean filterByTitleTechnology(JobOfferResponse offer) {
        String title = offer.title().toLowerCase();
        return title.contains("java");
    }

    private static boolean filterByUrlPlace(JobOfferResponse offer) {
        String url = offer.offerUrl().toLowerCase();
        return url.contains("remote") || url.contains("warsaw") || url.contains("warszawa");
    }
}

