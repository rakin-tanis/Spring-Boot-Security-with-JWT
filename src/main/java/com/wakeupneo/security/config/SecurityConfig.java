package com.wakeupneo.security.config;

import com.wakeupneo.security.component.CustomAccessDeniedHandler;
import com.wakeupneo.security.component.CustomAuthenticationEntryPoint;
import com.wakeupneo.security.component.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.wakeupneo.security.api.BaseController.BASE_PATH;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/js/**",
            "/css/**",
            "/img/**",
            "/",
            "/index",
            "/swagger-ui.html",
            BASE_PATH + "auth/register",
            BASE_PATH + "auth/emailLogin",
            BASE_PATH + "auth/usernameLogin",
            BASE_PATH + "auth/forgetPassword",
            BASE_PATH + "auth/validatePasswordResetToken",
            BASE_PATH + "auth/updateForgottenPassword",
            BASE_PATH + "auth/existEmail",
            BASE_PATH + "auth/existUsername",
            BASE_PATH + "auth/verifyAccount",
    };


    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests((req) -> req
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}