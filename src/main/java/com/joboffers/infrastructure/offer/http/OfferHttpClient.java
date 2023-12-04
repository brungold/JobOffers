package com.joboffers.infrastructure.offer.http;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.domain.offer.dto.JobOfferResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class OfferHttpClient implements OfferFetchable {

    private final RestTemplate restTemplate;
    private final String uri;
    private final int port;

    //    http://ec2-3-120-147-150.eu-central-1.compute.amazonaws.com:5057/offers
    @Override
    public List<JobOfferResponse> fetchAllOffers() {
        log.info("Started fetching offers using http client");

        HttpHeaders headers = createHeader();
        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        String uriForService = getUrlForService("/offers");
        final String url = UriComponentsBuilder.fromHttpUrl(uriForService).toUriString();

        ResponseEntity<List<JobOfferResponse>> response = makeGetRequest(requestEntity, url);
        return handleResponse(response);
    }

    private static HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String getUrlForService(String service) {
        return uri + ":" + port + service;
    }

    private ResponseEntity<List<JobOfferResponse>> makeGetRequest(HttpEntity<HttpHeaders> requestEntity, String url) {
        try {
            ResponseEntity<List<JobOfferResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    });
            return response;
        } catch (ResourceAccessException e) {
            log.error("Error while fetching offers using http client: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static List<JobOfferResponse> handleResponse(ResponseEntity<List<JobOfferResponse>> response) {
        final List<JobOfferResponse> body = response.getBody();
        if (body == null) {
            log.error("Response Body was null");
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        log.info("Success Response Body Returned: " + body);
        return body;
    }
}
