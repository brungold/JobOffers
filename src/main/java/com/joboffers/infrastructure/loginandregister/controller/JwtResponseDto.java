package com.joboffers.infrastructure.loginandregister.controller;

public record JwtResponseDto(
        String username,
        String token
) {
}
