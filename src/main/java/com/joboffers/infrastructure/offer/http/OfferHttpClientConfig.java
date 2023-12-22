package com.joboffers.infrastructure.offer.http;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.infrastructure.offer.http.nofluffjobsclient.OfferHttpClient;
import com.joboffers.infrastructure.offer.http.pracujplscrapperclient.OfferHttpPracujPl;
import com.joboffers.infrastructure.offer.http.pracujplscrapperclient.PracujPlService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OfferHttpClientConfig {

    // GET https://it.pracuj.pl:443/praca/junior%20java;kw/warszawa;wp?rd=30
    @Bean
    public RestTemplateResponseErrorHandler restTemplateResponseErrorHandler() {
        return new RestTemplateResponseErrorHandler();
    }

    @Bean
    @Qualifier("noFluffJobsRestTemplate")
    public RestTemplate restTemplate(@Value("${offer.http.client.config.connectionTimeout:1000}") long connectionTimeout,
                                     @Value("${offer.http.client.config.readTimeout:1000}") long readTimeout,
                                     RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
        return new RestTemplateBuilder()
                .errorHandler(restTemplateResponseErrorHandler)
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Bean
    public OfferFetchable remoteOfferClient(RestTemplate restTemplate,
                                            @Value("${offer.http.client.config.uri:http://example.com}") String uri,
                                            @Value("${offer.http.client.config.port:5057}") int port
    ) {
        return new OfferHttpClient(restTemplate, uri, port);
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
            @Value("${offer.http.client.pracujpl.uri:http://example.com}") String uri,
            @Value("${offer.http.client.pracujpl.port:443}") int port) {
        return new OfferHttpPracujPl(pracujPlRestTemplate, uri , port);
    }
}