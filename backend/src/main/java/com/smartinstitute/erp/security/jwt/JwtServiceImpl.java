package com.smartinstitute.erp.security.jwt;

import com.smartinstitute.erp.common.enums.JwtTokenType;
import com.smartinstitute.erp.security.userdetails.CustomUserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token,
                                UserDetails userDetails) {

        String username = extractUsername(token);

//        String tokenType = extractClaim(
//                token,
//                claims -> claims.get(JwtClaims.TOKEN_TYPE, String.class)
//        );

        return username.equals(userDetails.getUsername())
//                && JwtTokenType.ACCESS.name().equals(tokenType)
                && !isTokenExpired(token);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {

        return generateToken(
                userDetails,
                JwtTokenType.ACCESS,
                jwtProperties.getAccessTokenExpiration()
        );
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {

        return generateToken(
                userDetails,
                JwtTokenType.REFRESH,
                jwtProperties.getRefreshTokenExpiration()
        );
    }

    @Override
    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token,
                               Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(UserDetails userDetails,
                                 JwtTokenType tokenType,
                                 long expiration) {

        CustomUserDetails user = (CustomUserDetails) userDetails;

        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtClaims.USER_ID, user.getUserId());
        claims.put(JwtClaims.EMAIL, user.getUsername());
        claims.put(JwtClaims.ROLE, user.getRole());
        claims.put(JwtClaims.STATUS, user.getUser().getStatus().name());

        /*
         * Future Multi-Tenant ERP Support
         *
         * Uncomment once Institute entity relation is available.
         *
         * claims.put(
         *      JwtClaims.INSTITUTE_ID,
         *      user.getUser().getInstitute().getId()
         * );
         */

        claims.put(JwtClaims.TOKEN_TYPE, tokenType.name());

        return buildToken(
                claims,
                userDetails,
                expiration
        );
    }

    private String buildToken(
            Map<String, Object> claims,
            UserDetails userDetails,
            long expiration) {

        Date now = new Date();

        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public String extractTokenType(String token) {

        return extractClaim(
                token,
                claims -> claims.get(JwtClaims.TOKEN_TYPE, String.class)
        );
    }

    public Claims extractClaims(String token) {

        return extractAllClaims(token);
    }
}