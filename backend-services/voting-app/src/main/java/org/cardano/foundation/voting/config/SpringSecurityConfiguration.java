package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.auth.jwt.JwtFilter;
import org.cardano.foundation.voting.service.auth.web3.Web3Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

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
                .cors().and()
                .csrf().disable()

                .addFilterBefore(web3Filter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .sessionManagement()
                .disable()

                .rememberMe()
                .disable()

                // SECURED by JWT auth
                .authorizeHttpRequests()
                .requestMatchers(GET, "/api/vote/receipt/**")
                .authenticated()

                .and()

                // SECURED by Web3 auth
                .authorizeHttpRequests()
                .requestMatchers(GET, "/api/vote/receipt")
                .authenticated()

                .and()

                // SECURED by Web3 auth
                .authorizeHttpRequests()
                .requestMatchers(GET, "/api/auth/login")
                .authenticated()

                .and()

                // SECURED by Web3 auth
                .authorizeHttpRequests()
                .requestMatchers(POST, "/api/vote/cast")
                .authenticated()

                .and()

                .authorizeHttpRequests()
                .requestMatchers(GET, "/api/leaderboard/**")
                .permitAll()

                .and()

                .authorizeHttpRequests()
                .requestMatchers(GET, "/actuator/**")
                .permitAll()

                .and()

                .authorizeHttpRequests()
                .requestMatchers("/h2-console")
                .permitAll()

                .and()

                .authorizeHttpRequests()
                .anyRequest()
                .denyAll()

                .and()

                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS);

        return http.build();
    }

}
