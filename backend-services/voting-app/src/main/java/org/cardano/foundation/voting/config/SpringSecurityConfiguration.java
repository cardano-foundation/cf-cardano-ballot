package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.auth.JwtAuthenticationEntryPoint;
import org.cardano.foundation.voting.service.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SpringSecurityConfiguration {

    @Autowired
    private SecurityProblemSupport problemSupport;

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

                .sessionManagement()
                .disable()

                .rememberMe()
                .disable()

                // SECURED by JWT auth
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/api/vote/receipt/**")
                .fullyAuthenticated()

                .and()

                .authorizeHttpRequests()
                .anyRequest()
                .permitAll()

                .and()

                .exceptionHandling()
                //.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS);

        return http.build();
    }

}