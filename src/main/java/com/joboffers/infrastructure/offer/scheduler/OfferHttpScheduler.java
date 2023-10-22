package com.joboffers.infrastructure.offer.scheduler;

import com.joboffers.domain.offer.OfferFacade;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class OfferHttpScheduler {
    private final OfferFacade offerFacade;

    @Scheduled(cron = "")
    public void f() {
        log.info("");
    }
}
