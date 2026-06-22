package com.bazario.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Logger instance for logging JWT-related operations
     * such as token generation, validation, and errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;


    /**
     * JWT Authentication Filter that intercepts each HTTP request.
     *
     * Responsibilities:
     * - Extract JWT token from Authorization header
     * - Validate the token using JwtTokenProvider
     * - Retrieve username and load user details from database
     * - Extract roles/authorities from token
     * - Create Authentication object and set it in SecurityContext
     *
     * If token is valid, the user is authenticated for the current request lifecycle.
     * Otherwise, the request proceeds without authentication.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try{
            String jwt = extractTokenFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.isTokenValid(jwt))
            {
                String userName = jwtTokenProvider.getUserNameFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);


                List<SimpleGrantedAuthority> authorities = jwtTokenProvider
                        .getRolesFromToken(jwt)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


                UsernamePasswordAuthenticationToken authenticate = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authenticate.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticate);
            }
        }
        catch (Exception e){
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }



    /**
     * Extracts the JWT token from the HTTP Authorization header.
     *
     * The method checks if the Authorization header contains a Bearer token.
     * If present, it removes the "Bearer " prefix and returns the actual token.
     *
     * Returns null if the header is missing or not in the expected format.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
