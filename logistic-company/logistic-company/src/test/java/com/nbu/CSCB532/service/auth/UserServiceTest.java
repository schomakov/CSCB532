package com.nbu.CSCB532.service.auth;

import com.nbu.CSCB532.model.Role;
import com.nbu.CSCB532.model.auth.User;
import com.nbu.CSCB532.repository.auth.UserRepository;
import com.nbu.CSCB532.service.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("rawpassword")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    void registerUser_success_setsDefaultRoleAndEncodesPassword() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawpassword")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        user.setRole(null);
        User saved = userService.registerUser(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.CLIENT);
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(saved).isNotNull();
    }

    @Test
    void registerUser_duplicateUsername_throwsUserAlreadyExistException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("Username");
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void registerUser_duplicateEmail_throwsUserAlreadyExistException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void findByUsername_whenExists_returnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertThat(userService.findByUsername("testuser")).contains(user);
    }

    @Test
    void findByUsername_whenNotExists_returnsEmpty() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThat(userService.findByUsername("unknown")).isEmpty();
    }

    @Test
    void findAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> result = userService.findAll();
        assertThat(result).hasSize(1).containsExactly(user);
    }

    @Test
    void updateUserRole_updatesAndSaves() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        userService.updateUserRole(1L, Role.EMPLOYEE);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.EMPLOYEE);
    }

    @Test
    void updateUserRole_userNotFound_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUserRole(999L, Role.EMPLOYEE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteById_callsRepository() {
        userService.deleteById(1L);
        verify(userRepository).deleteById(1L);
    }
}
