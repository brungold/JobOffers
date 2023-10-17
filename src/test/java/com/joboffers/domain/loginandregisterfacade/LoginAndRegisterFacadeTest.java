package com.joboffers.domain.loginandregisterfacade;

import com.joboffers.domain.loginandregisterfacade.dto.RegisterUserDto;
import com.joboffers.domain.loginandregisterfacade.dto.RegistrationResultDto;
import com.joboffers.domain.loginandregisterfacade.dto.UserDto;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

class LoginAndRegisterFacadeTest {
    LoginAndRegisterFacade loginFacade = new LoginAndRegisterFacade(
            new InMemoryLoginRepository()
    );

    @Test
    public void should_find_user_by_user_name() {
        //given
        RegisterUserDto registerUserDto = new RegisterUserDto("username", "pass");
        RegistrationResultDto register = loginFacade.register(registerUserDto);

        //when
        UserDto userByName = loginFacade.findByUserName(register.username());

        //then
        assertThat(userByName).isEqualTo(new UserDto(register.id(), "username", "pass"));
    }

    @Test
    public void should_throw_exception_when_user_not_found() {
        //given
        String username = "someUser";

        //when
        Throwable thrown = catchThrowable(() -> loginFacade.findByUserName(username));

        //then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found.");
    }

    @Test
    public void should_register_user() {
        //given
        RegisterUserDto registerUserDto = new RegisterUserDto("username", "pass");

        //when
        RegistrationResultDto register = loginFacade.register(registerUserDto);

        //then
        assertAll(
                () -> assertThat(register.created()).isTrue(),
                () -> assertThat(register.username()).isEqualTo("username")
        );
    }

}