package com.joboffers.infrastructure.offer.http.pracujplscrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.domain.offer.dto.JobOfferResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.joboffers.infrastructure.offer.http.pracujplscrapper.PracujPlMapper.mapFromJobOfferNodesToListJobOfferResponse;

@AllArgsConstructor
@Log4j2
public class OfferHttpPracujPlScrapper implements OfferFetchable {
    private final RestTemplate restTemplatePracujPl;
    private final String uri;
    private final int port;

    public List<JobOfferResponse> fetchAllOffers() {
        String url = buildUrl();
        String responseBody = fetchResponseBody(url);
        Document parsedDocument = parseHtml(responseBody);
        String jsonText = extractJsonText(parsedDocument);
        List<JobOfferResponse> response = parseJson(jsonText);
        return response;
    }

    // GET https://it.pracuj.pl/praca/junior%20java;kw/warszawa;wp?rd=30

    private List<JobOfferResponse> parseJson(String jsonText) {
        log.info("Started fetching offers from Pracuj.p using http client and jsoup");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);
            JsonNode jobOffersNode = jsonNode.at("/props/pageProps/data/jobOffers/groupedOffers");


            List<JobOfferResponse> mappedJobOfferResponses = mapFromJobOfferNodesToListJobOfferResponse(jobOffersNode);
            log.info("Success Response Body Returned: " + mappedJobOfferResponses);
            return PracujPlService.filterBySeniorityTechnologyAndPlaceInUrl(mappedJobOfferResponses);

        } catch (ResourceAccessException e) {
            log.error("Error while fetching offers from Pracuj.pl using http client and jsoup: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error processing JSON: " + e.getMessage());
        }
    }

    private String fetchResponseBody(String url) {
        ResponseEntity<String> responseEntity = restTemplatePracujPl.getForEntity(url, String.class);
        return responseEntity.getBody();
    }

    private String buildUrl() {
//        String baseUrl = "https://it.pracuj.pl/praca/";
        String searchQuery = "junior%20java;kw";
        String location = "warszawa;wp?rd=30";
        return String.format("%s%s/%s", uri, searchQuery, location);
    }



    private Document parseHtml(String html) {
        return Jsoup.parse(html);
    }

    private String extractJsonText(Document document) {
        Element scriptElement = document.selectFirst("script#__NEXT_DATA__");
        return scriptElement.html();
    }
}
