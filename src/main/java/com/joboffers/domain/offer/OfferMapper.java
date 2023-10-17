package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.JobOfferResponse;
import com.joboffers.domain.offer.dto.OfferRequestDto;
import com.joboffers.domain.offer.dto.OfferResponseDto;

public class OfferMapper {
    public static OfferResponseDto mapFromOferToOfferResponseDto(Offer offer) {
        return OfferResponseDto.builder()
                .id(offer.id())
                .companyName(offer.companyName())
                .position(offer.position())
                .salary(offer.salary())
                .offerUrl(offer.offerUrl())
                .build();
    }

    public static Offer mapFromOfferDtoToOffer(OfferRequestDto offerRequestDto) {
        return Offer.builder()
                .companyName(offerRequestDto.companyName())
                .position(offerRequestDto.position())
                .salary(offerRequestDto.salary())
                .offerUrl(offerRequestDto.offerUrl())
                .build();
    }

    public static Offer mapFromJobOfferResponseToOffer(JobOfferResponse jobOfferResponseDto) {
        return Offer.builder()
                .companyName(jobOfferResponseDto.company())
                .salary(jobOfferResponseDto.wages())
                .position(jobOfferResponseDto.station())
                .offerUrl(jobOfferResponseDto.offerUrl())
                .build();
    }
}
