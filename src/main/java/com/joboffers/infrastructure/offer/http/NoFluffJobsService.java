package com.joboffers.infrastructure.offer.http;

import com.joboffers.domain.offer.dto.JobOfferResponse;
import com.joboffers.infrastructure.offer.http.dto.DraftForFilteringJobOfferResponseDto;
import com.joboffers.infrastructure.offer.http.dto.DraftListForFilteringJobOfferResponseDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoFluffJobsService {

    public static List<JobOfferResponse> getFilteredOffers(DraftListForFilteringJobOfferResponseDto draftListForFilteringJobOfferResponseDto) {
        List<DraftForFilteringJobOfferResponseDto> mappedOffers = NoFluffJobsMapper.mapToJobRecordDtoList(draftListForFilteringJobOfferResponseDto);
        List<DraftForFilteringJobOfferResponseDto> filteredOffers = filterByFlavorsAndSeniority(mappedOffers);
        List<DraftForFilteringJobOfferResponseDto> filteredByPlacesOffers = filterByPlaces(filteredOffers);
        List<DraftForFilteringJobOfferResponseDto> finalOffers = filterAndDistinctByCity(filteredByPlacesOffers);
        return NoFluffJobsMapper.mapFromJobRecordDtoListJobRecordDtoList(finalOffers);

    }

    public static List<DraftForFilteringJobOfferResponseDto> filterByFlavorsAndSeniority(List<DraftForFilteringJobOfferResponseDto> jobRecords) {
        return jobRecords.stream()
                .filter(record -> containsSeniority(record.seniority())
                        && containsTitle(record.title()))
                .collect(Collectors.toList());
    }

    public static List<DraftForFilteringJobOfferResponseDto> filterByPlaces(List<DraftForFilteringJobOfferResponseDto> jobRecords) {
        return jobRecords.stream()
                .filter(jobRecord -> containsKeyword(jobRecord.url()))
                .collect(Collectors.toList());
    }

    private static boolean containsKeyword(String url) {
        List<String> keywords = Arrays.asList("remote", "warsaw", "warszawa");
        String urlWithoutHyphens = url.replace("-", " ");
        return keywords.stream().anyMatch(keyword -> containsPlace(urlWithoutHyphens, keyword));
    }

    public static List<DraftForFilteringJobOfferResponseDto> filterAndDistinctByCity(List<DraftForFilteringJobOfferResponseDto> jobRecordDtos) {
        return jobRecordDtos.stream()
                .filter(jobRecordDto ->
                        isRemote(jobRecordDto.url()) || containsCity(jobRecordDto.url(), "Warsaw") || containsCity(jobRecordDto.url(), "warszawa"))
                .collect(Collectors.toList());
    }

    private static boolean isRemote(String url) {
        // Sprawdź, czy URL zawiera słowo "remote"
        return url.toLowerCase().contains("remote");
    }

    private static boolean containsCity(String text, String city) {
        // Sprawdź, czy tekst zawiera daną nazwę miasta
        return text.toLowerCase().contains(city.toLowerCase());
    }

    private static boolean containsSeniority(List<String> seniority) {
        return seniority
                .stream()
                .anyMatch(s -> isJuniorOrMlodszy(s));
    }

    private static boolean isJuniorOrMlodszy(String seniority) {
        return seniority.equalsIgnoreCase("junior") ||
                seniority.equalsIgnoreCase("młodszy") ||
                seniority.equalsIgnoreCase("mlodszy");
    }

    private static boolean containsPlace(String text, String word) {
        // Sprawdzenie, czy tekst zawiera dane słowo (case insensitive)
        return Arrays.stream(text.split("\\s+"))
                .map(String::toLowerCase)
                .anyMatch(word::equalsIgnoreCase);
    }

    private static boolean containsTitle(String title) {
        return title.toLowerCase().contains("java");
    }
}



