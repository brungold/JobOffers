package com.joboffers.domain.offer;

import com.joboffers.domain.offer.error.OfferDuplicateException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOfferRepository implements OfferRepository {

    Map<String, Offer> database = new ConcurrentHashMap<>();

    @Override
    public List<Offer> findAll() {
        return database.values().stream().toList();
    }

    @Override
    public Optional<Offer> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public boolean existsByUrl(String url) {
        for (Offer offer : database.values()) {
            if (offer.offerUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Offer save(Offer entity) {
        if (database.values().stream().anyMatch(offer -> offer.offerUrl().equals(entity.offerUrl()))) {
            throw new OfferDuplicateException(entity.offerUrl());
        }
        UUID id = UUID.randomUUID();
        Offer offer = new Offer(
                id.toString(),
                entity.companyName(),
                entity.position(),
                entity.salary(),
                entity.offerUrl()
        );
        database.put(id.toString(), offer);
        return offer;
    }

    @Override
    public List<Offer> saveAll(List<Offer> offers) {
        return offers.stream()
                .map(offer -> save(offer))
                .toList();
    }
}
