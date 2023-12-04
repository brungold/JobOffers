package com.joboffers.infrastructure.offer.http.pracujplscrapper;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.domain.offer.dto.JobOfferResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Log4j2
public class OfferHttpPracujPlScrapper implements OfferFetchable {
    private final RestTemplate restTemplatePracujPl;
    private final String uri;
    private final int port;

    // GET https://it.pracuj.pl/praca/junior%20java;kw/warszawa;wp?rd=30
    // GET https://it.pracuj.pl/praca/junior%20java;kw

    public List<JobOfferResponse> fetchAllOffers() {
        String url = buildUrl();
        String responseBody = fetchResponseBody(url);
        return parseHtml(responseBody);
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

    private String fetchResponseBody(String url) {
        ResponseEntity<String> responseEntity = restTemplatePracujPl.getForEntity(url, String.class);
        return responseEntity.getBody();
    }

    private String buildUrl() {
        String searchQuery = "/junior%20java;kw";
        String location = "warszawa;wp?rd=30";
        return String.format("%s%s/%s", uri, searchQuery, location);
    }

//    private String buildUrl() {
//        String searchQuery = "junior%20java;kw";
//        String location = "warszawa;wp";
//        String rdParameter = "30";
//
//        return UriComponentsBuilder.fromHttpUrl(uri)
//                .pathSegment(searchQuery, location)
//                .queryParam("rd", rdParameter)
//                .build().toString();
//    }
}
