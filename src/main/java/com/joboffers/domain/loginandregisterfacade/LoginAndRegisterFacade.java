package com.joboffers.domain.loginandregisterfacade;

import com.joboffers.domain.loginandregisterfacade.dto.RegisterUserDto;
import com.joboffers.domain.loginandregisterfacade.dto.RegistrationResultDto;
import com.joboffers.domain.loginandregisterfacade.dto.UserDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginAndRegisterFacade {

    private static final String USER_NOT_FOUND = "User not found.";

    private final LoginRepository repository;

    public UserDto findByUserName(String username) {
        return repository.findByUserName(username)
                .map(user -> new UserDto(user.id(), user.username(), user.password()))
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
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