package org.duyvu.carbooking.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

	private final String secretKey;

	private final Duration expiration;

	public enum ClaimAttribute {
		ROLE("role"),
		ID("id");

		private final String key;

		ClaimAttribute(String key) {
			this.key = key;
		}
	}

	public JwtUtils(@Value("${security.jwt.secret-key}") String secretKey, @Value("${security.jwt.expiration-time}") Duration expiration) {
		this.secretKey = secretKey;
		this.expiration = expiration;
	}

	public String generateAccessToken(String username, Map<ClaimAttribute, Object> claims) {
		return createToken(claims.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().key, Map.Entry::getValue)),
						   username, expiration);
	}

	public String generateRefreshToken(String username) {
		return createToken(Map.of(), username, expiration.multipliedBy(5));
	}

	private String createToken(Map<String, Object> claims, String username, Duration expiration) {
		return Jwts.builder()
				   .claims(claims)
				   .subject(username)
				   .issuedAt(new Date())
				   .expiration(new Date(System.currentTimeMillis() + expiration.getSeconds() * 1000))
				   .signWith(getSignKey())
				   .compact();
	}

	private SecretKey getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Long extractId(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get(ClaimAttribute.ID.key, Long.class);
	}

	public LocalDateTime extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String extractRole(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get(ClaimAttribute.ROLE.key, String.class);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				   .verifyWith(getSignKey())
				   .build()
				   .parseSignedClaims(token)
				   .getPayload();
	}

	public boolean isValidateToken(String token) {
		return extractExpiration(token).isAfter(LocalDateTime.now());
	}
}
