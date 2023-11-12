package com.wakeupneo.security.component;

import com.wakeupneo.security.config.prop.JwtProp;
import com.wakeupneo.security.model.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String AUTHORITIES = "authorities";
    public static final String TOKEN_CAN_NOT_BE_VERIFIED = "Token can not be verified";
    public static final String AUTHORITY = "authority";

    private final JwtProp jwtProp;
    private final SecretKey secretKey;
    private final UserDetailsService userDetailsService;

    public String generateToken(UserPrincipal userPrincipal) {
        Clock now = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        Clock expired = Clock.offset(now, Duration.ofDays(jwtProp.getTokenExpirationAfterDays()));
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim(AUTHORITIES, userPrincipal.getAuthorities())
                .issuer(jwtProp.getIssuer())
                .audience().add(jwtProp.getAudience()).and()
                .issuedAt(new Date(Instant.now().toEpochMilli()))
                .expiration(new Date(expired.instant().toEpochMilli()))
                .signWith(secretKey)
                .compact();
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        return Arrays.stream(getClaimsFromToken(token))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private String[] getClaimsFromToken(String token) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> authorities = (List<Map<String, String>>) getClaims(token).get(AUTHORITIES);
            return authorities
                    .stream()
                    .map(authorityMap -> authorityMap.get(AUTHORITY))
                    .toArray(String[]::new);
        } catch (JwtException e) {
            log.error(e.getMessage());
            throw new JwtException(TOKEN_CAN_NOT_BE_VERIFIED);
        }

    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith(secretKey) // or decryptWith(secretKey)
                .requireIssuer(jwtProp.getIssuer())
                .requireAudience(jwtProp.getAudience())
                .build();
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    private Claims getClaims(String token) {
        return getJwtParser().parseSignedClaims(token).getPayload();
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token)
                .getExpiration()
                .before(new Date(Instant.now().toEpochMilli()));
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        String username = getSubject(token);
        if (StringUtils.isNotEmpty(username) && !isTokenExpired(token)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            List<GrantedAuthority> authorities = getAuthorities(token);
            return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        }
        return null;
    }

}
