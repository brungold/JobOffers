package com.joboffers.infrastructure.offer.scheduler;

import com.joboffers.domain.offer.OfferFacade;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class OfferHttpScheduler {
    private final OfferFacade offerFacade;

    @Scheduled(cron = "")
    public List<OfferResponseDto> fetchAllOffersAndSaveAllIfNotExists() {
        log.info("Fetching offers has started");
        List<OfferResponseDto> offerResponseDtosList = offerFacade.fetchAllOffersAndSaveAllIfNotExists();
        return offerResponseDtosList;
    }
}
