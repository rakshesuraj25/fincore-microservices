package com.ServiceIMPL;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.Security.CustomUserService;
import com.servcie.JWTService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long jwtExpiration;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(
            Map<String, Object> extraClaims,
            CustomUserService userDetails) {

        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    @Override
    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            CustomUserService userDetails,
            long expiration) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(
                        getSignInKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    @Override
    public boolean isTokenValid(
            String token,
            CustomUserService userDetails) {

        final String username = extractUsername(token);

        return username.equals(userDetails.getEmail())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractTokenFromRequest(
            HttpServletRequest request) {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (StringUtils.hasText(authorizationHeader)
                && authorizationHeader.startsWith("Bearer ")) {

            return authorizationHeader.substring(7);
        }

        return null;
    }

    @Override
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        if (claimsResolver == null) {
            log.warn("Claims resolver is null");
            return null;
        }

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(CustomUserService userDetails) {
        return generateToken(
                new HashMap<>(),
                userDetails
        );
    }
}