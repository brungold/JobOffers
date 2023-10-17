package com.joboffers.domain.offer;

import java.util.List;
import java.util.Optional;

public interface OfferRepository {
    List<Offer> findAll();

    Optional<Offer> findById(String id);

    Offer save(Offer offer);

    List<Offer> saveAll(List<Offer> offers);

    boolean existsByUrl(String url);
}