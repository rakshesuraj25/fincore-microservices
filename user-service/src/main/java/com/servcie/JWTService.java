package com.servcie;

import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import com.Security.CustomUserService;

import io.jsonwebtoken.Claims;

public interface JWTService {

	String extractUsername(String token);

	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);

	String generateToken(CustomUserService userDetails);

	String generateToken(Map<String, Object> extraClaims, CustomUserService userDetails);

	long getExpirationTime();

	boolean isTokenValid(String token, CustomUserService userDetails);

	String extractTokenFromRequest(HttpServletRequest request);
	
	
	
	
	
	
}
