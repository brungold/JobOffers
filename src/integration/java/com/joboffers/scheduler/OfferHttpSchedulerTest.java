package com.joboffers.scheduler;

import com.joboffers.BaseIntegrationTest;
import com.joboffers.JobOffersSpringBootApplication;
import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.domain.offer.OfferService;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = JobOffersSpringBootApplication.class, properties = "scheduling.enabled=true")
public class OfferHttpSchedulerTest extends BaseIntegrationTest {
    @SpyBean
    @Qualifier("remoteOfferClient")
    OfferFetchable remoteOfferClient;

    @SpyBean
    @Qualifier("remoteOfferClientPracujPl")
    OfferFetchable remoteOfferClientPracujPl;


    @Test
    public void should_run_http_client_offers_fetching_exactly_given_times() {
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(remoteOfferClient, times(1)).fetchAllOffers();
                    verify(remoteOfferClientPracujPl, times(1)).fetchAllOffers();
                });
        }
}
