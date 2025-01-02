package com.example.demo.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private String secretKey = "giakhiem";

    @Value("${jwt.expiration}")
    private long expiration;

    // Kiểm tra token có hợp lệ không
    public boolean validateToken(String token) {
        try {
            // Dùng parserBuilder() thay cho parser() từ phiên bản jjwt mới
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // Khóa bí mật để kiểm tra chữ ký
                    .build()
                    .parseClaimsJws(token);   // Kiểm tra token, bao gồm chữ ký và hạn sử dụng
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token đã hết hạn");
        } catch (MalformedJwtException e) {
            System.out.println("Token không hợp lệ");
        } catch (SignatureException e) {
            System.out.println("Chữ ký không hợp lệ");
        } catch (IllegalArgumentException e) {
            System.out.println("Token rỗng");
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Khóa bí mật để giải mã token
                .build()
                .parseClaimsJws(token)     // Giải mã token và lấy các claims
                .getBody();
        return claims.getSubject();   // Subject thường là username
    }
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateJwtToken(String  username) {
        // Tạo JWT token
        return Jwts.builder()
                .setSubject(username) // Đặt username của người dùng làm subject
                .setIssuedAt(new Date()) // Thời gian phát hành token
                .setExpiration(new Date((new Date()).getTime() + 360000)) // Thời gian hết hạn
                .signWith(getSigningKey()) // Sử dụng thuật toán PS384 và khóa bí mật
                .compact();  // Trả về JWT token đã được tạo
    }




}
