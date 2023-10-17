package com.joboffers.domain.loginandregisterfacade;

import lombok.Builder;

@Builder
public record User(String id,
                   String username,
                   String password) {
}
