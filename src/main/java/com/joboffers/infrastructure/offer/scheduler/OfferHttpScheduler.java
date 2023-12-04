package com.joboffers.infrastructure.offer.scheduler;

import com.joboffers.domain.offer.OfferFacade;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class OfferHttpScheduler {
    private final OfferFacade offerFacade;
    private static final String STARTED_OFFERS_FETCHING_MESSAGE = "Started offers fetching {}";
    private static final String FINISHED_OFFERS_FETCHING_MESSAGE = "Finished offers fetching {}";
    private static final String ADDED_NEW_OFFERS_MESSAGE = "Added new {} offers";
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Scheduled(fixedDelayString = "${http.offers.scheduler.request.delay}")
    public List<OfferResponseDto> fetchAllOffersPracujPlAndSaveAllIfNotExists() {
        log.info(STARTED_OFFERS_FETCHING_MESSAGE, LocalDateTime.now().format(dateFormat));
        List<OfferResponseDto> offerResponseDtosList = offerFacade.fetchAllOffersPracujPlAndSaveAllIfNotExists();
        log.info(ADDED_NEW_OFFERS_MESSAGE, offerResponseDtosList.size());
        log.info(FINISHED_OFFERS_FETCHING_MESSAGE, LocalDateTime.now().format(dateFormat));
        return offerResponseDtosList;
    }

    @Scheduled(fixedDelayString = "${http.offers.scheduler.request.delay}")
    public List<OfferResponseDto> fetchAllOffersFromNoFluffJobsAndSaveAllIfNotExists() {
        log.info(STARTED_OFFERS_FETCHING_MESSAGE, LocalDateTime.now().format(dateFormat));
        List<OfferResponseDto> offerResponseDtosList = offerFacade.fetchAllOffersFromNoFluffJobsAndSaveAllIfNotExists();
        log.info(ADDED_NEW_OFFERS_MESSAGE, offerResponseDtosList.size());
        log.info(FINISHED_OFFERS_FETCHING_MESSAGE, LocalDateTime.now().format(dateFormat));
        return offerResponseDtosList;
    }
}
