package com.example.tourismsystem.config;

import com.example.tourismsystem.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
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
                                // ВСЕГДА сначала разрешаем публичные endpoints
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/api/destinations").permitAll()
                                .requestMatchers("/api/tours/available").permitAll()
                                .requestMatchers("/api/guides").permitAll()

                                // Затем защищенные endpoints
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/guides/**").hasAnyRole("GUIDE", "ADMIN")
                                .requestMatchers("/api/bookings/**").authenticated()
                                .requestMatchers("/api/reviews").authenticated()

                                // ОСТОРОЖНО: этот паттерн может перекрывать другие!
                                .requestMatchers(HttpMethod.GET, "/api/tours").permitAll()  // Разрешить просмотр
                                .requestMatchers(HttpMethod.POST, "/api/tours").authenticated() // Создание только для авторизованных

                                .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}