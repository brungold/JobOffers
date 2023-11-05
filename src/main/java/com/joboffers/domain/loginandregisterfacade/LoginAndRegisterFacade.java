package com.joboffers.domain.loginandregisterfacade;

import com.joboffers.domain.loginandregisterfacade.dto.RegisterUserDto;
import com.joboffers.domain.loginandregisterfacade.dto.RegistrationResultDto;
import org.springframework.security.authentication.BadCredentialsException;
import com.joboffers.domain.loginandregisterfacade.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LoginAndRegisterFacade {

    private static final String USER_NOT_FOUND = "User not found.";

    private final LoginRepository repository;

    public UserDto findByUserName(String username) {
        return repository.findByUsername(username)
                .map(user -> new UserDto(user.id(), user.username(), user.password()))
                .orElseThrow(() -> new BadCredentialsException(USER_NOT_FOUND));
    }

    public RegistrationResultDto register(RegisterUserDto registerUserDto) {
        final User user = User.builder()
                .username(registerUserDto.username())
                .password(registerUserDto.password())
                .build();
        User savedUser = repository.save(user);
        return new RegistrationResultDto(savedUser.id(), true, savedUser.username());
    }
}