package de.ruu.lib.keycloak.admin.validation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility for parsing and extracting claims from JWT tokens without requiring external libraries.
 * 
 * <p>This is a lightweight parser for debugging and validation purposes. 
 * For production token validation, use proper JWT libraries like jose4j or nimbus-jose-jwt.
 * 
 * @author r-uu
 * @since 2025-12-27
 */
@Slf4j
public class JwtTokenParser
{
	/**
	 * Parses a JWT token and extracts common claims.
	 * 
	 * @param token JWT token string (format: header.payload.signature)
	 * @return parsed token information
	 * @throws IllegalArgumentException if token format is invalid
	 */
	public static TokenInfo parseToken(String token)
	{
		if (token == null || token.isBlank())
		{
			throw new IllegalArgumentException("Token cannot be null or blank");
		}
		
		String[] parts = token.split("\\.");
		if (parts.length != 3)
		{
			throw new IllegalArgumentException("Invalid JWT format. Expected 3 parts, got " + parts.length);
		}
		
		try
		{
			// Decode header
			String headerJson = decodeBase64Url(parts[0]);
			
			// Decode payload
			String payloadJson = decodeBase64Url(parts[1]);
			
			// Parse JSON manually (avoiding Jackson dependency in lib module)
			Map<String, Object> payload = parseSimpleJson(payloadJson);
			
			return extractTokenInfo(payload);
		}
		catch (Exception e)
		{
			log.error("Failed to parse JWT token", e);
			throw new IllegalArgumentException("Failed to parse JWT token: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Decodes Base64URL encoded string.
	 */
	private static String decodeBase64Url(String base64Url)
	{
		// Replace URL-safe characters with standard Base64
		String base64 = base64Url
				.replace('-', '+')
				.replace('_', '/');
		
		// Add padding if needed
		int padding = (4 - base64.length() % 4) % 4;
		base64 += "=".repeat(padding);
		
		byte[] decoded = Base64.getDecoder().decode(base64);
		return new String(decoded, StandardCharsets.UTF_8);
	}
	
	/**
	 * Simple JSON parser for extracting JWT claims.
	 * Only supports simple structures needed for JWT tokens.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> parseSimpleJson(String json)
	{
		Map<String, Object> result = new HashMap<>();
		
		// Remove outer braces and whitespace
		json = json.trim();
		if (json.startsWith("{")) json = json.substring(1);
		if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
		
		// Simple parsing - manually split by comma, respecting nested structures
		List<String> pairs = new ArrayList<>();
		int bracketDepth = 0;
		int arrayDepth = 0;
		boolean inQuotes = false;
		StringBuilder current = new StringBuilder();
		
		for (char c : json.toCharArray())
		{
			if (c == '"' && (current.length() == 0 || current.charAt(current.length() - 1) != '\\'))
			{
				inQuotes = !inQuotes;
			}
			else if (!inQuotes)
			{
				if (c == '{') bracketDepth++;
				else if (c == '}') bracketDepth--;
				else if (c == '[') arrayDepth++;
				else if (c == ']') arrayDepth--;
				else if (c == ',' && bracketDepth == 0 && arrayDepth == 0)
				{
					pairs.add(current.toString());
					current = new StringBuilder();
					continue;
				}
			}
			current.append(c);
		}
		if (current.length() > 0)
		{
			pairs.add(current.toString());
		}
		
		for (String pair : pairs)
		{
			pair = pair.trim();
			int colonIndex = pair.indexOf(":");
			if (colonIndex <= 0) continue;
			
			String key = pair.substring(0, colonIndex).trim();
			String value = pair.substring(colonIndex + 1).trim();
			
			// Remove quotes from key
			key = key.replaceAll("\"", "");
			
			// Parse value
			if (value.startsWith("[") && value.endsWith("]"))
			{
				// Array value
				value = value.substring(1, value.length() - 1);
				List<String> arrayValues = Arrays.stream(value.split(","))
						.map(String::trim)
						.map(s -> s.replaceAll("\"", ""))
						.filter(s -> !s.isEmpty())
						.collect(Collectors.toList());
				result.put(key, arrayValues);
			}
			else if (value.startsWith("{") && value.endsWith("}"))
			{
				// Nested object - recursively parse
				result.put(key, parseSimpleJson(value));
			}
			else if (value.equals("true") || value.equals("false"))
			{
				// Boolean
				result.put(key, Boolean.parseBoolean(value));
			}
			else if (value.matches("-?\\d+"))
			{
				// Number (long)
				result.put(key, Long.parseLong(value));
			}
			else
			{
				// String - remove quotes
				result.put(key, value.replaceAll("\"", ""));
			}
		}
		
		return result;
	}
	
	/**
	 * Extracts relevant token information from parsed payload.
	 */
	@SuppressWarnings("unchecked")
	private static TokenInfo extractTokenInfo(Map<String, Object> payload)
	{
		// Extract standard claims
		String issuer = (String) payload.get("iss");
		String subject = (String) payload.get("sub");
		Long exp = (Long) payload.get("exp");
		Long iat = (Long) payload.get("iat");
		
		// Extract audience (can be string or array)
		Set<String> audiences = new HashSet<>();
		Object aud = payload.get("aud");
		if (aud instanceof String)
		{
			audiences.add((String) aud);
		}
		else if (aud instanceof List)
		{
			audiences.addAll((List<String>) aud);
		}
		
		// Extract roles from realm_access
		Set<String> roles = new HashSet<>();
		Object realmAccess = payload.get("realm_access");
		if (realmAccess instanceof Map)
		{
			Object rolesObj = ((Map<String, Object>) realmAccess).get("roles");
			if (rolesObj instanceof List)
			{
				roles.addAll((List<String>) rolesObj);
			}
		}
		
		// Extract preferred_username
		String preferredUsername = (String) payload.get("preferred_username");
		
		return TokenInfo.builder()
				.issuer(issuer)
				.subject(subject)
				.audiences(audiences)
				.roles(roles)
				.preferredUsername(preferredUsername)
				.expirationTime(exp)
				.issuedAt(iat)
				.build();
	}
	
	/**
	 * Represents parsed JWT token information.
	 */
	@Value
	@lombok.Builder
	public static class TokenInfo
	{
		String issuer;
		String subject;
		Set<String> audiences;
		Set<String> roles;
		String preferredUsername;
		Long expirationTime;  // Unix timestamp in seconds
		Long issuedAt;        // Unix timestamp in seconds
		
		/**
		 * Checks if the token is expired.
		 */
		public boolean isExpired()
		{
			if (expirationTime == null) return false;
			return System.currentTimeMillis() / 1000 > expirationTime;
		}
		
		/**
		 * Gets remaining lifetime in seconds.
		 */
		public long getRemainingLifetimeSeconds()
		{
			if (expirationTime == null) return -1;
			long remaining = expirationTime - (System.currentTimeMillis() / 1000);
			return Math.max(0, remaining);
		}
		
		/**
		 * Gets token lifetime in seconds (exp - iat).
		 */
		public long getLifetimeSeconds()
		{
			if (expirationTime == null || issuedAt == null) return -1;
			return expirationTime - issuedAt;
		}
	}
}
