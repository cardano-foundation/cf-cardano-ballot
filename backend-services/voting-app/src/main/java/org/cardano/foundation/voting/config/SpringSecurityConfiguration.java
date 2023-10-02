package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.auth.jwt.JwtFilter;
import org.cardano.foundation.voting.service.auth.web3.Web3Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SpringSecurityConfiguration {

    @Autowired
    private SecurityProblemSupport problemSupport;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private Web3Filter web3Filter;

    @Value("${cors.allowed.origins:http://localhost:3000}")
    private String allowedUrls;

    @ConditionalOnProperty( //to make sure it is active if console is enabled
            value="spring.h2.console.enabled",
            havingValue = "true",
            matchIfMissing = false)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
              .cors(c ->
                c.configurationSource(request -> {
                    var cors = new CorsConfiguration();
                    cors.setAllowedMethods(List.of("GET", "HEAD", "POST"));
                    cors.setAllowedHeaders(List.of("*"));

                    cors.setAllowedOrigins(Arrays.stream(allowedUrls.split(",")).toList());

                    return cors;
                }))
                .csrf(AbstractHttpConfigurer::disable)

                .addFilterBefore(web3Filter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .sessionManagement(AbstractHttpConfigurer::disable)

                .rememberMe(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(requests -> {
                    requests
                    // SECURED by Web3 auth
                    .requestMatchers(new AntPathRequestMatcher("/api/vote/cast", POST.name())).authenticated()
                    // SECURED by JWT auth
                    .requestMatchers(new AntPathRequestMatcher("/api/vote/votes/**", GET.name())).authenticated()
                    // SECURED by JWT auth
                    .requestMatchers(new AntPathRequestMatcher("/api/vote/receipt/**", GET.name())).authenticated()
                    // SECURED by Web3 auth
                    .requestMatchers(new AntPathRequestMatcher("/api/vote/receipt", GET.name())).authenticated()
                    // SECURED by Web3 auth
                    .requestMatchers(new AntPathRequestMatcher("/api/auth/login", GET.name())).authenticated()
                    // SECURED by JWT auth
                    //.requestMatchers(new AntPathRequestMatcher("/api/vote/vote-changing-available/**", HEAD.name())).authenticated()

                    // without auth
                    .requestMatchers(new AntPathRequestMatcher("/api/leaderboard/**", GET.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/leaderboard/**", HEAD.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/actuator/**", GET.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                    .anyRequest().denyAll();
                })

                .exceptionHandling(c -> c.accessDeniedHandler(problemSupport).authenticationEntryPoint(problemSupport))
                .sessionManagement(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
