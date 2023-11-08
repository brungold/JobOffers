package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.OfferRequestDto;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import com.joboffers.domain.offer.error.OfferNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

@AllArgsConstructor
public class OfferFacade {
    private final OfferRepository offerRepository;
    private final OfferService offerService;

    @Cacheable(cacheNames = "jobOffers")
    public List<OfferResponseDto> findAllOffers() {
        return offerRepository.findAll()
                .stream()
                .map(OfferMapper::mapFromOferToOfferResponseDto)
                .toList();
    }

    public OfferResponseDto findOfferById(String id) {
        return offerRepository.findById(id)
                .map(OfferMapper::mapFromOferToOfferResponseDto)
                .orElseThrow(() -> new OfferNotFoundException(id));
    }

    public OfferResponseDto saveOffer(OfferRequestDto offerRequestDto) {
        final Offer offer = OfferMapper.mapFromOfferDtoToOffer(offerRequestDto);
        final Offer offerSaved = offerRepository.save(offer);
        return OfferMapper.mapFromOferToOfferResponseDto(offerSaved);
    }

    public List<OfferResponseDto> fetchAllOffersAndSaveAllIfNotExists() {
        return offerService.fetchAllOffersAndSaveAllIfNotExists()
                .stream()
                .map(OfferMapper::mapFromOferToOfferResponseDto)
                .toList();

    }
}
