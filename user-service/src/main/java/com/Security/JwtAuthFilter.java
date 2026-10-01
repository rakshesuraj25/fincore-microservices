package com.Security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.servcie.JWTService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/v1/auth/loginuser",
            "/api/v1/loginuser",
            "/api/v1/auth/registeruser",
            "/api/v1/registeruser",
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui/index.html",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config"
    );

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        log.debug("Incoming request URI: {}", path);

        // Skip JWT authentication for public endpoints
        for (String excluded : EXCLUDED_PATHS) {

            if (path.startsWith(excluded)) {

                log.debug(
                        "Skipping JWT filter for public path: {}",
                        path
                );

                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");

        // No JWT token -> continue the filter chain
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            log.debug("No Bearer authentication token found");

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String jwt = authHeader.substring(7);

            String userEmail =
                    jwtService.extractUsername(jwt);

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (userEmail != null
                    && authentication == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(
                        jwt,
                        (CustomUserService) userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);

                    log.debug(
                            "JWT authentication successful for: {}",
                            userEmail
                    );

                } else {

                    log.warn(
                            "Invalid or expired JWT for user: {}",
                            userEmail
                    );
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception exception) {

            log.error(
                    "JWT authentication failed: {}",
                    exception.getMessage()
            );

            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    exception
            );
        }
    }
}