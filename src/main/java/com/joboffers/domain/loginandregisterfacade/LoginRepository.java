package com.joboffers.domain.loginandregisterfacade;

import java.util.Optional;

public interface LoginRepository {
    Optional<User> findByUserName(String username);

    User save(User user);
}
