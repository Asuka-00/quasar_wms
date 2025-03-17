package com.wms.wmsend.common.util;

import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final long tokenExpiration =  60 * 60 * 1000L;
    private static final long refreshTokenExpiration = 60 * 60 * 24 * 7 * 1000L;
    private static final SecretKey tokenSignKey = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());

    public static String createToken(Long userId, String username) {
        String token = Jwts.builder().
                setSubject("USER_INFO").
                setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)).
                claim("userId", userId).
                claim("username", username).
                signWith(tokenSignKey).
                compact();
        return token;
    }

    public static String createRefreshToken(Long userId, String username) {
        String token = Jwts.builder().
                setSubject("REFRESH_TOKEN").
                setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)).
                claim("userId", userId).
                claim("username", username).
                signWith(tokenSignKey).
                compact();
        return token;
    }
    public static Claims parseToken(String token){

        if (token==null){
            throw new WmsException(ResultCodeEnum.LOGIN_AUTH);
        }
        try{
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenSignKey).build();
            return jwtParser.parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            throw new WmsException(ResultCodeEnum.TOKEN_EXPIRED);
        }catch (JwtException e){
            throw new WmsException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(createRefreshToken(16L,"user01"));
    }
}
