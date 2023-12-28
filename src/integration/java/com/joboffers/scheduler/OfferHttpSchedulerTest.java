package com.joboffers.scheduler;

import com.joboffers.BaseIntegrationTest;
import com.joboffers.JobOffersSpringBootApplication;
import com.joboffers.domain.offer.OfferFetchable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(classes = JobOffersSpringBootApplication.class, properties = "scheduling.enabled=true")
public class OfferHttpSchedulerTest extends BaseIntegrationTest {
    @MockBean
    @Qualifier("remoteOfferClient")
    OfferFetchable remoteOfferClient;

    @MockBean
    @Qualifier("remoteOfferClientPracujPl")
    OfferFetchable remoteOfferClientPracujPl;
    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @DynamicPropertySource
    public static void propertyOverridePracujPl (DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    public void should_run_http_client_offers_fetching_exactly_given_times() {
        await().atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    verify(remoteOfferClient, times(2)).fetchAllOffers();
                    verify(remoteOfferClientPracujPl, times(2)).fetchAllOffers();
                });
        }
}
