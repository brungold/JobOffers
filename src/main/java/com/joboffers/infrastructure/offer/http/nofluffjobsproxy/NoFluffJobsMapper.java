package com.joboffers.infrastructure.offer.http.nofluffjobsproxy;

import com.joboffers.domain.offer.dto.JobOfferResponse;
import com.joboffers.infrastructure.offer.http.nofluffjobsproxy.dto.DraftForFilteringJobOfferResponseDto;
import com.joboffers.infrastructure.offer.http.nofluffjobsproxy.dto.DraftListForFilteringJobOfferResponseDto;
import com.joboffers.infrastructure.offer.http.nofluffjobsproxy.dto.Salary;

import java.util.List;
import java.util.stream.Collectors;

public class NoFluffJobsMapper {
    public static List<JobOfferResponse> mapFromJobRecordDtoListJobRecordDtoList(List<DraftForFilteringJobOfferResponseDto> jobRecordDtos) {
        return jobRecordDtos.stream()
                .map(NoFluffJobsMapper::mapJobRecordDto)
                .collect(Collectors.toList());
    }

    public static List<DraftForFilteringJobOfferResponseDto> mapToJobRecordDtoList(DraftListForFilteringJobOfferResponseDto offerResponseDto) {
        return offerResponseDto.postings().stream()
                .map(NoFluffJobsMapper::mapToJobRecordDto)
                .collect(Collectors.toList());
    }

    private static DraftForFilteringJobOfferResponseDto mapToJobRecordDto(DraftForFilteringJobOfferResponseDto jobRecordDto) {
        return new DraftForFilteringJobOfferResponseDto(
                jobRecordDto.name(),
                jobRecordDto.title(),
                jobRecordDto.seniority(),
                jobRecordDto.url(),
                jobRecordDto.salary()
        );
    }

    private static JobOfferResponse mapJobRecordDto(DraftForFilteringJobOfferResponseDto jobRecordDto) {
        String salary = mapSalary(jobRecordDto.salary());
        String offerUrl = buildFullUrl(jobRecordDto.url());
        return JobOfferResponse.builder()
                .title(jobRecordDto.title())
                .company(jobRecordDto.name())
                .salary(salary)
                .offerUrl(offerUrl)
                .build();
    }

    private static String mapSalary(Salary salary) {
        if (salary != null) {
            String from = salary.from() != null ? salary.from().toString() : "N/A";
            String to = salary.to() != null ? salary.to().toString() : "N/A";
            return from + " - " + to + " " + salary.currency();
        } else {
            return "N/A";
        }
    }

    private static String buildFullUrl(String endpoint) {
        String baseUrl = "https://nofluffjobs.com/pl/job/";
        return baseUrl + endpoint;
    }
}
