package com.joboffers.infrastructure.offer.http;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.domain.offer.dto.JobOfferResponse;
import com.joboffers.infrastructure.offer.http.dto.DraftListForFilteringJobOfferResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@Log4j2
public class OfferHttpClient implements OfferFetchable {

    private final RestTemplate restTemplate;
    private final String uri;
    private final int port;


    //    GET https://nofluffjobs.com/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl
    //    GET https://nofluffjobs.com:433/api/posting?salaryCurrency=PLN&salaryPeriod=month&region=pl
    //    https://nofluffjobs.com/api/posting

    @Override
    public List<JobOfferResponse> fetchAllOffers() {
        log.info("Started fetching offers using http client");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        try {
            final String url = getUrlForService();
            ResponseEntity<DraftListForFilteringJobOfferResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    });
            DraftListForFilteringJobOfferResponseDto body = response.getBody();
            if (body == null) {
                log.error("Response Body was null");
                throw new ResponseStatusException(HttpStatus.NO_CONTENT);
            }
            log.info("Success Response Body Returned: " + body);
            return NoFluffJobsService.getFilteredOffers(body);
        } catch (ResourceAccessException e) {
            log.error("Error while fetching offers using http client: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getUrlForService() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                .port(port)
                .path("/api/posting")
                .queryParam("salaryCurrency", "PLN")
                .queryParam("salaryPeriod", "month")
                .queryParam("region", "pl");

        return builder.toUriString();
    }
}
