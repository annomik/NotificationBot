package com.example.notificationbot.config;

import com.example.notificationbot.model.UserRole;
import com.example.notificationbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        httpSecurity
                .authenticationManager(authenticationManager)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/users/login", "/users/activate/**", "/users/new").permitAll()
                                .requestMatchers("/tasks/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.USER.name())
                                .requestMatchers("/users/**").hasRole(UserRole.ADMIN.name())
                                .anyRequest().authenticated()

                ).formLogin(
                        form -> form
                                .loginPage("/users/login")
                                .loginProcessingUrl("/process_login")
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/users/login?error")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/users/login")
                );


        return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userService
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
