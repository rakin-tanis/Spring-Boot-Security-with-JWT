package com.wakeupneo.security.component;

import com.google.common.base.Strings;
import com.wakeupneo.security.config.prop.JwtProp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpMethod.OPTIONS;

@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProp jwtProp;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getMethod().equalsIgnoreCase(OPTIONS.name())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            String authorizationHeader = request.getHeader(jwtProp.getAuthorizationHeader());
            if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtProp.getTokenPrefix())) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.replace(jwtProp.getTokenPrefix(), "").trim();
            UsernamePasswordAuthenticationToken authentication =
                    (UsernamePasswordAuthenticationToken) jwtTokenProvider.getAuthentication(token);
            if (authentication != null) {
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
