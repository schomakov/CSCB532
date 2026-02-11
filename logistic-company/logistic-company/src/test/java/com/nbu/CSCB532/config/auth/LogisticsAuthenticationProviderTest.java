package com.nbu.CSCB532.config.auth;

import com.nbu.CSCB532.model.Role;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogisticsAuthenticationProviderTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LogisticsAuthenticationProvider authenticationProvider;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded")
                .email("test@test.com")
                .role(Role.CLIENT)
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Test
    void authenticate_validCredentials_returnsAuthentication() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        var auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        var result = authenticationProvider.authenticate(auth);
        assertThat(result).isNotNull();
        assertThat(result.getPrincipal()).isEqualTo("testuser");
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("CLIENT");
    }

    @Test
    void authenticate_wrongPassword_throwsBadCredentials() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);
        var auth = new UsernamePasswordAuthenticationToken("testuser", "wrong");
        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticate_unknownUser_throwsBadCredentials() {
        when(userService.findByUsername("unknown")).thenReturn(Optional.empty());
        var auth = new UsernamePasswordAuthenticationToken("unknown", "password");
        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void supports_returnsTrue() {
        assertThat(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
    }
}
