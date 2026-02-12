package com.nbu.CSCB532.config;

import com.nbu.CSCB532.config.auth.LogisticsAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final LogisticsAuthenticationProvider authenticationProvider;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/login", "/register", "/css/**", "/js/**", "/*/api/**").permitAll()
                                .requestMatchers("/admin/**").hasAnyAuthority("ADMINISTRATOR")
                                .requestMatchers("/employee/**", "/courier/**").hasAnyAuthority("EMPLOYEE", "ADMINISTRATOR")
                                .requestMatchers("/client/**").hasAnyAuthority("CLIENT", "ADMINISTRATOR")
                                .requestMatchers("/reports/revenue", "/reports/revenue/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers("/reports/**").hasAnyAuthority("EMPLOYEE", "ADMINISTRATOR")
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(login -> login.loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .authenticationProvider(authenticationProvider);

        return http.build();
    }
}
