package com.joboffers.infrastructure.offer.http.scrapperpracujpl;

import com.joboffers.domain.offer.dto.JobOfferResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class ScrapperPracujPl {
    private final RestTemplate restTemplate;
    public List<JobOfferResponse> fetchPracujPlOffers() {
        String url = buildUrl();
        String responseBody = fetchResponseBody(url);
        Document parsedDocument = parseHtml(responseBody);
        String jsonText = extractJsonText(parsedDocument);
        return parseJson(jsonText);
    }

    private String buildUrl() {
        String baseUrl = "https://it.pracuj.pl/praca/";
        String searchQuery = "junior%20java;kw";
        String location = "warszawa;wp?rd=30";
        return String.format("%s%s/%s", baseUrl, searchQuery, location);
    }

    private String fetchResponseBody(String url) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return responseEntity.getBody();
    }

    private Document parseHtml(String html) {
        return Jsoup.parse(html);
    }

    private String extractJsonText(Document document) {
        Element scriptElement = document.selectFirst("script#__NEXT_DATA__");
        return scriptElement.html();
    }

    private List<JobOfferResponse> parseJson(String jsonText) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<JobOfferResponse> jobOffers = new ArrayList<>();

            JsonNode jsonNode = objectMapper.readTree(jsonText);
            JsonNode jobOffersNode = jsonNode.at("/props/pageProps/data/jobOffers/groupedOffers");

            for (JsonNode jobOfferNode : jobOffersNode) {
                String jobTitle = jobOfferNode.at("/jobTitle").asText();
                String companyName = jobOfferNode.at("/companyName").asText();
                String salaryDisplayText = jobOfferNode.at("/salaryDisplayText").asText();
                String offerAbsoluteUri = jobOfferNode.at("/offers/0/offerAbsoluteUri").asText();

                JobOfferResponse jobOfferResponse = new JobOfferResponse(jobTitle, companyName, salaryDisplayText, offerAbsoluteUri);
                jobOffers.add(jobOfferResponse);
            }

            return jobOffers;

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception or throw a custom exception
            return new ArrayList<>();
        }
    }
}
