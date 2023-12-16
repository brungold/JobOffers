package com.joboffers.infrastructure.offer.http.pracujplscrapperclient;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.domain.offer.dto.JobOfferResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Log4j2
public class OfferHttpPracujPl implements OfferFetchable {
    // GET https://it.pracuj.pl/praca/junior%20java;kw/warszawa;wp?rd=30

    private final RestTemplate restTemplatePracujPl;
    private final String uri;
    private final int port;

    public List<JobOfferResponse> fetchAllOffers() {
        log.info("Started fetching offers from PacujPl using http client");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        try {
            String url = buildUrl();
            log.info("Started connecting with Pracuj.pl. Request URL: {}", url);
//            ResponseEntity<String> responseEntity = restTemplatePracujPl.getForEntity(url, String.class, httpEntity);
            ResponseEntity <String> responseEntity = restTemplatePracujPl.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    String.class);
            final String body = responseEntity.getBody();
            if (body == null) {
                log.error("Response Body was null");
                throw new ResponseStatusException(HttpStatus.NO_CONTENT);
            }
            log.info("Success Response body returned: " + body);
            return parseHtml(body);
        } catch (ResourceAccessException exception) {
            log.error("Error while fetching offers using http client for Pracuj.pl: " + exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private List<JobOfferResponse> parseHtml(String html) {
        log.info("Started fetching offers from Pracuj.p using http client and jsoup");
        Document document = Jsoup.parse(html);

        Elements jobOfferElements = document.select("div.c13gi8t");

        List<JobOfferResponse> jobOffers = jobOfferElements.stream()
                .map(offerElement -> {
                    Element titleElement = offerElement.selectFirst("h2[data-test='offer-title'] a");
                    String jobTitle = (titleElement != null) ? titleElement.text() : "Unknown Title";

                    Element companyElement = offerElement.selectFirst("h4[data-test='text-company-name']");
                    String companyName = (companyElement != null) ? companyElement.text() : "Unknown Company";

                    Element linkElement = titleElement != null ? titleElement : offerElement.selectFirst("a[data-test='link-offer']");
                    String offerUrl = (linkElement != null) ? linkElement.attr("href") : "";

                    Element salaryElement = offerElement.selectFirst("span[data-test='offer-salary']");
                    String salary = (salaryElement != null) ? salaryElement.text() : "Not Available";

                    return new JobOfferResponse(jobTitle, companyName, salary, offerUrl);
                })
                .collect(Collectors.toList());

        log.info("Success Response Body Returned: " + jobOffers);
        return PracujPlService.filterBySeniorityTechnologyAndPlaceInUrl(jobOffers);
    }

    // https://it.pracuj.pl:433/praca/junior%20java;kw/warszawa;wp?rd=30
    private String buildUrl() {
        // String path = "/praca/junior%20java;kw"; - %20 bez encodowania daje spacje
        String path = "/praca/junior java;kw";
        String city = "warszawa;wp";
        String queryParamName = "rd";
        String queryParamValue = "30";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                .port(port)
                .path(path)
                .pathSegment(city)
                .queryParam(queryParamName, queryParamValue)
                .encode();

        return builder.toUriString();
    }
}
