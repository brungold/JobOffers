package com.joboffers;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(classes = JobOffersSpringBootApplication.class)
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Testcontainers
public class BaseIntegrationTest {

    public static final String WIRE_MOCK_HOST_PRACUJ = "http://localhost";
    public static final String WIRE_MOCK_HOST_NOFLUFFJOBS = "http://localhost";

    @Autowired
    public MockMvc mockMvc;

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Autowired
    public ObjectMapper objectMapper;

    @RegisterExtension
    public static WireMockExtension wireMockServerForPracuj = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @RegisterExtension
    public static WireMockExtension wireMockServerForNoFluffJobs = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    public static void propertyOverrideNoFluffJobs (DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("offer.http.client.nofluffjobs.uri", () -> WIRE_MOCK_HOST_NOFLUFFJOBS);
        registry.add("offer.http.client.nofluffjobs.port", () -> wireMockServerForNoFluffJobs.getPort());
    }

    @DynamicPropertySource
    public static void propertyOverridePracujPl (DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("offer.http.client.pracujpl.uri", () -> WIRE_MOCK_HOST_PRACUJ);
        registry.add("offer.http.client.pracujpl.port", () -> wireMockServerForPracuj.getPort());
    }
}
