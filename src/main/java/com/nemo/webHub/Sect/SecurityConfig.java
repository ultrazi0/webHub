package com.nemo.webHub.Sect;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // to disable use AbstractHttpConfigurer::disable
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/api/register", "/api/csrf").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/api/login")
                        .permitAll()
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
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        // Keep Spring Security updated about session lifecycle events

        return new HttpSessionEventPublisher();
    }
}
