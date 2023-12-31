package com.joboffers.http.offer;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.infrastructure.offer.http.OfferHttpClientConfig;
import org.springframework.web.client.RestTemplate;

import static com.joboffers.BaseIntegrationTest.WIRE_MOCK_HOST;

public class OfferHttpRestTemplateIntegrationTestConfig extends OfferHttpClientConfig {

    public OfferFetchable remonteOfferFetchable(int port, int connectionTimeout, int readTimeout) {
        final RestTemplate restTemplate = restTemplate(connectionTimeout, readTimeout, restTemplateResponseErrorHandler());
        return remoteOfferClient(restTemplate, WIRE_MOCK_HOST, port);
    }


}
