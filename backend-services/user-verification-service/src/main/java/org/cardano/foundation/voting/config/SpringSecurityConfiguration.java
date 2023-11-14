package org.cardano.foundation.voting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@Import(SecurityProblemSupport.class)
public class SpringSecurityConfiguration {

    @Autowired
    private SecurityProblemSupport problemSupport;

    @Value("${cors.allowed.origins:http://localhost:3000}")
    private String allowedUrls;

    @Value("${discord.bot.username:discord_bot}")
    private String discordBotUsername;

    @Value("${discord.bot.password}")
    private String discordBotPassword;

    @ConditionalOnProperty( //to make sure it is active if console is enabled
            value="spring.h2.console.enabled",
            havingValue = "true",
            matchIfMissing = false)

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        var basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("DISCORD");

        return basicAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(AbstractHttpConfigurer::disable)
            .cors(c -> c.configurationSource(request -> {
                var cors = new CorsConfiguration();
                cors.setAllowedMethods(List.of("GET", "POST", "PUT"));
                cors.setAllowedHeaders(List.of("*"));

                var urls = Arrays.stream(allowedUrls.split(",")).toList();

                cors.setAllowedOrigins(urls);

                return cors;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .anonymous(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers(new AntPathRequestMatcher("/api/sms/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/discord/user-verification/is-verified/**", GET.name())).hasRole("BOT")
                    .requestMatchers(new AntPathRequestMatcher("/api/discord/user-verification/start-verification", POST.name())).hasRole("BOT")
                    .requestMatchers(new AntPathRequestMatcher("/api/discord/user-verification/start-verification", PUT.name())).hasRole("BOT")
                    .requestMatchers(new AntPathRequestMatcher("/api/discord/user-verification/check-verification", POST.name())).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/api/user-verification/verified/**", GET.name())).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/actuator/**", GET.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()

                    .anyRequest().denyAll()
         )
         .rememberMe(AbstractHttpConfigurer::disable)
         .anonymous(AbstractHttpConfigurer::disable)
         .formLogin(AbstractHttpConfigurer::disable)
         .logout(AbstractHttpConfigurer::disable)

        .httpBasic(c -> c.authenticationEntryPoint(authenticationEntryPoint()))
        .exceptionHandling(c -> c.authenticationEntryPoint(problemSupport)
        .accessDeniedHandler(problemSupport));

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User
                .withUsername(discordBotUsername)
                .password(passwordEncoder().encode(discordBotPassword))
                .roles("BOT")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

}