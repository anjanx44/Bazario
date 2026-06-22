package com.bazario.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {
    /**
     * Logger instance for logging JWT-related operations
     * such as token generation, validation, and errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * Secret key used for signing and validating JWT tokens.
     *
     * Value is injected from application.yml configuration:
     * jwt.secret
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT token expiration time in milliseconds.
     *
     * Defines how long a generated token remains valid.
     * Value is injected from application.yml configuration:
     * jwt.expiration-ms
     */
    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;


    /**
     * Generates the HMAC signing key used for JWT token signing and validation.
     *
     * The secret key is stored in Base64 encoded format and decoded
     * before being converted into a SecretKey instance.
     *
     * This key is used to sign JWT tokens and verify their integrity.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Generates a JWT token for the authenticated user.
     *
     * The token includes:
     * - username as the subject
     * - user roles as a custom claim
     * - issued and expiration timestamps
     *
     * This token is signed using the application secret key
     * and is used for securing API endpoints.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles",roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * Internally parses and validates the token before retrieving
     * the subject claim, which represents the authenticated user.
     */
    public String getUserNameFromToken(String token) {
        return parseClaims(token).getSubject();
    }


    /**
     * Extracts user roles from the JWT token.
     *
     * Reads the custom "role" claim from the token payload
     * and casts it to a List of strings.
     *
     * Assumes that roles are stored as a list during token generation.
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return (List<String>) parseClaims(token).get("roles");
    }

    /**
     * Validates the JWT token by attempting to parse its claims.
     *
     * If the token is valid, properly signed, and not expired,
     * it returns true. Otherwise, it catches specific JWT exceptions
     * and logs the corresponding error, returning false.
     *
     * Handles cases such as:
     * - Malformed token
     * - Expired token
     * - Unsupported token format
     * - Empty or invalid claims
     */
    public boolean isTokenValid(String token) {
        try{
            parseClaims(token);
            return true;
        }catch(MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    /**
     * Parses and validates the JWT token to extract its claims (payload).
     *
     * This method verifies the token signature using the signing key
     * before decoding it, ensuring the token has not been tampered with.
     *
     * Returns the claims (payload) which contain user data such as
     * username, roles, and other custom information.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
