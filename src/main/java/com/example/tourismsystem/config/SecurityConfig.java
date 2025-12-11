package com.example.tourismsystem.config;

import com.example.tourismsystem.security.jwt.JwtAuthenticationFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
                // Временно отключаем CSRF для тестирования JWT
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                // Публичные endpoints
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/destinations").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/tours/available").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/guides").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/tours").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/tours/**").permitAll()

                                // Защищенные по ролям
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/guides/**").hasAnyRole("GUIDE", "ADMIN")

                                // Защищенные (требуют аутентификации)
                                .requestMatchers(HttpMethod.POST, "/api/tours").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/tours/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/tours/**").authenticated()
                                .requestMatchers("/api/bookings/**").authenticated()
                                .requestMatchers("/api/reviews").authenticated()

                                .anyRequest().authenticated()
                )
                // Добавляем JWT фильтр перед UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}