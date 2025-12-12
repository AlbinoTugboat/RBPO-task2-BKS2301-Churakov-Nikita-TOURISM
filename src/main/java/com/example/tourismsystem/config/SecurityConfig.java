package com.example.tourismsystem.config;

import com.example.tourismsystem.security.jwt.JwtAuthenticationFilter;
import com.example.tourismsystem.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false); // Не скрывать исключения
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(authenticationProvider()));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/auth/login").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/destinations").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/tours/available").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/guides").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/tours").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/tours/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/guides/**").hasAnyRole("GUIDE", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/tours").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/tours/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/tours/**").authenticated()
                                .requestMatchers("/api/bookings/**").authenticated()
                                .requestMatchers("/api/reviews").authenticated()
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}