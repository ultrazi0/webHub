package com.nemo.rexus.Sect;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_STATIC_RESOURCES = {
        "/favicon.ico", "/index.html", "/manifest.json", "/robots.txt", "/assets/**", "/logo192.png", "/logo512.png"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(Customizer.withDefaults())  // to disable use AbstractHttpConfigurer::disable
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(PUBLIC_STATIC_RESOURCES).permitAll()
                    .requestMatchers("/", "/error", "/api/register", "/api/csrf").permitAll()
                    .requestMatchers("/api/command/robot", "/api/image/robot").hasRole("ROBOT")
                    .requestMatchers("/api", "/swagger-ui/*", "/v3/api-docs/*", "/v3/api-docs").permitAll()
                    .anyRequest().hasRole("USER"))
                .formLogin(form -> form
                        .loginPage("/api/login").permitAll()
                        .successHandler((request, response, authentication) -> {
                            // do nothing
                        }).failureHandler(((request, response, exception) -> {
                            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());  // Send CSRF token in a header for the next attempt
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
                        })))
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT)))) // No redirect
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(((request, response, authException) ->
                                        // Whenever authentication is required, it does not redirect to login page (default), but just sends 401
                                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))
                                )
                                .accessDeniedHandler(((request, response, accessDeniedException) -> {
                                    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                                    response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());  // Send CSRF token in a header for the next attempt
                                    response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());  // Do what is default i.e. return 403
                                })))
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))  // No cache so that pre-login requests are not cached
                .securityContext(context -> context.requireExplicitSave(true))  // explicitly sets SecurityContextHolderFilter (even though it is a default option)
                .sessionManagement(session -> session.maximumSessions(1));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserRepositoryUserDetailsService userRepositoryUserDetailsService,
            RobotRepositoryUserDetailsService robotRepositoryUserDetailsService,
            PasswordEncoder passwordEncoder) {
        /*
         * TODO: for some reason Spring Security still issues a warning saying that two UserDetailsService beans
         *  are initialized, even though a custom AuthenticationManager bean is provided.
         *  This happens in InitializeUserDetailsManagerConfigurer#configure
         */

        DaoAuthenticationProvider userAuthenticationProvider = new DaoAuthenticationProvider(userRepositoryUserDetailsService);
        userAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        DaoAuthenticationProvider robotAuthenticationProvider = new DaoAuthenticationProvider(robotRepositoryUserDetailsService);

        return new ProviderManager(robotAuthenticationProvider, userAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        // Keep Spring Security updated about session lifecycle events

        return new HttpSessionEventPublisher();
    }
}
