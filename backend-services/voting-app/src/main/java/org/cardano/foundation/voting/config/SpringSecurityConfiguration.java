package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.auth.JwtAuthenticationEntryPoint;
import org.cardano.foundation.voting.service.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                // Add a filter to validate the tokens with every request
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests()
                .requestMatchers("/api/leaderboard/**").permitAll()

                .and()

                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()

                .and()

                // TODO enable for vote's receipt
                .authorizeHttpRequests()
                .requestMatchers("/api/vote/**").permitAll()

                .and()

                .authorizeHttpRequests()
                .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()

                .and()

                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()

                .and()

                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS);

        return http.build();
    }

}