package com.yabng.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * JWT工具类 - 提供JWT令牌的生成、解析和验证功能
 */
public class JwtUtil {
    
    /**
     * 默认过期时间(毫秒) - 24小时
     */
    private static final long DEFAULT_EXPIRATION = 86400000L;

    private static final String KEY = "asdfgadfihaiufhiqwerfquifhgqauifhaijfdiowuq98ru89qhfqw811rhhfskafnashjklzxcv1213";
    
    /**
     * 密钥 - 实际项目中应该从配置文件读取
     */
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(KEY.getBytes(StandardCharsets.UTF_8));
    
    /**
     * 生成JWT令牌
     * @param claims 自定义载荷信息
     * @return JWT令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return generateToken(claims, DEFAULT_EXPIRATION);
    }
    
    /**
     * 生成JWT令牌
     * @param claims 自定义载荷信息
     * @param expiration 过期时间(毫秒)
     * @return JWT令牌字符串
     */
    public static String generateToken(Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }
    
    /**
     * 解析JWT令牌
     * @param token JWT令牌字符串
     * @return Claims对象，包含载荷信息
     * @throws JwtException 当令牌无效时抛出异常
     */
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌字符串
     * @return true表示有效，false表示无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    /**
     * 从令牌中获取特定载荷值
     * @param token JWT令牌字符串
     * @param claimKey 载荷键名
     * @return 载荷值
     */
    public static Object getClaimFromToken(String token, String claimKey) {
        Claims claims = parseToken(token);
        return claims.get(claimKey);
    }
    
    /**
     * 检查令牌是否过期
     * @param token JWT令牌字符串
     * @return true表示已过期，false表示未过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
