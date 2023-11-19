package com.joboffers.infrastructure.offer.http;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.infrastructure.offer.http.nofluffjobsproxy.OfferHttpNoFluffJobsClient;
import com.joboffers.infrastructure.offer.http.pracujplscrapper.PracujPlService;
import com.joboffers.infrastructure.offer.http.pracujplscrapper.OfferHttpPracujPlScrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OfferHttpClientConfig {

    @Bean
    public RestTemplateResponseErrorHandler restTemplateResponseErrorHandler() {
        return new RestTemplateResponseErrorHandler();
    }

    @Bean
    @Qualifier("noFluffJobsRestTemplate")
    public RestTemplate restTemplate(
            @Value("${offer.http.client.nofluffjobs.connectionTimeout:5000}") long connectionTimeout,
            @Value("${offer.http.client.nofluffjobs.readTimeout:5000}") long readTimeout,
            RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
        return new RestTemplateBuilder()
                .errorHandler(restTemplateResponseErrorHandler)
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Bean
    public OfferFetchable remoteOfferNoFluffJobsClient(
            @Qualifier("noFluffJobsRestTemplate") RestTemplate restTemplate,
            @Value("${offer.http.client.nofluffjobs.uri:http://example.com}") String uri,
            @Value("${offer.http.client.nofluffjobs.port:443}") int port) {
        return new OfferHttpNoFluffJobsClient(restTemplate, uri, port);
    }

    @Bean
    @Qualifier("restTemplatePracujPl")
    public RestTemplate restTemplatePracujPl(@Value("${offer.http.client.pracujpl.connectionTimeout:5000}") long connectionTimeout,
                                             @Value("${offer.http.client.pracujpl.readTimeout:5000}") long readTimeout,
                                             RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
        return new RestTemplateBuilder()
                .errorHandler(restTemplateResponseErrorHandler)
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Bean
    public OfferFetchable remoteOfferClientPracujPl (
                                            @Qualifier("restTemplatePracujPl") RestTemplate pracujPlRestTemplate,
                                            PracujPlService pracujPlService,
                                            @Value("${offer.http.client.pracujpl.uri:http://example.com}") String uri,
                                            @Value("${offer.http.client.pracujpl.port:443}") int port) {
        return new OfferHttpPracujPlScrapper(pracujPlRestTemplate, uri , port);
    }
}