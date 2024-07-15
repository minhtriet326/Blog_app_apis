package com.blog.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//@Service
//public class JwtService {
//    //Đang đc mã hóa Base64
//    private static final String SECRET_KEY = "BF7FD11ACE545745B7BA1AF98B6F156D127BC7BB544BAB6A4FD74E4FC7";
//
//    //Tạo Jwt với thông tin và thời gian hết hạn
//    public String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // Tạo Jwt từ thông tin user
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, userDetails.getUsername());
//    }
//
//    //###
//
//    // Kiểm tra token hợp lệ
//    public Boolean isTokenValid(String token, UserDetails userDetails) {
//        String extractUsername = extractUsername(token);
//        return !isTokenExpired(token) && userDetails.getUsername().equals(extractUsername);
//    }
//
//    // Trích xuất tất cả thông tin từ Jwt (claims có "s")
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();// Lấy cả cái phần Payload
//    }
//
//    // Trích xuất thông tin cụ thể từ Jwt (claim không có "s")
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // extract username from JWT
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Trích xuất ngày hết hạn từ Jwt
//    public Date extractExpiration(String token) { return extractClaim(token, Claims::getExpiration); }
//
//    // kiểm tra hết hạn
//    private Boolean isTokenExpired(String token) {
//        Date tokenExpirationDate = extractExpiration(token);
//        return tokenExpirationDate.before(new Date());
//    }
//
//    // Hàm getSignInKey để trích xuất SignInKey
//    private Key getSignInKey() {// được sử dụng để chuyển đổi SECRET_KEY từ dạng String thành một đối tượng Key có thể sử dụng để ký và xác minh JWT
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);// giải mã
//        return Keys.hmacShaKeyFor(keyBytes);// tạo ra một đối tượng Key từ mảng keyBytes bằng cách dùng thuật toán HMAC-SHA.
//    }
//}
//@Service
//public class JwtService {
//    //Đang được mã hóa BASE64
//    private final String SECRET_KEY = "BF7FD11ACE545745B7BA1AF98B6F156D127BC7BB544BAB6A4FD74E4FC7";
//
//    //Hàm getSignInKey dùng để trích xuất SignInKey
//    private Key getSignInKey() {// hàm này dùng để tạo ra 1 đt Key từ 1 SECRET_KEY dạng String dùng cho kí và xác minh jwt
//        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);// giải mã
//        return Keys.hmacShaKeyFor(keyBytes);// trả về 1 đt Key từ mảng byte[] dùng thuật toán HMAC-SHA
//    }
//
//    //tạo JWT với thông tin và thời gian hết hạn
//    public String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    //tạo jwt từ thông tin user
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, userDetails.getUsername());
//    }
//
//    // extract all claims
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();// lấy trọn bộ payload.
//    }
//
//    //extract a claim
//    private <Z> Z extractClaim(String token, Function<Claims, Z> claimsResolver)
//    // Z là đại diện cho kdl bất kì, <Z> biểu thị rằng method này là generic và kiểu trả về là Z
//    // generic là method hoạt động được với mọi kdl
//    // Function<A, Z> nghĩa là đối số này là 1 function nhận vào 1 đt kiểu A và trả về gt kiểu Z
//    // interface này có 1 abstract method duy nhất là apply
//    // claimsResolver.apply(claims) nghĩa là áp dụng Function lên đối số claims
//    {
//        Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    //extract email
//    public String extractEmail(String token) {
//        return extractClaim(token, (Claims claims) -> claims.getSubject());
//    }
//
//    //extract expiration date
//    public Date extractExpirationDate(String token) {
//        return extractClaim(token, (Claims claims) -> claims.getExpiration());
//    }
//
//    // xác minh token hợp lệ
//    public Boolean isTokenValid(String token, UserDetails userDetails) {
//        return !isTokenExpired(token) && userDetails.getUsername().equals(extractEmail(token));
//    }
//
//    // xác minh token hết hạn
//    public Boolean isTokenExpired(String token) {
//        Date expirationDate = extractExpirationDate(token);
//        return expirationDate.before(new Date());
//    }
//}
@Service
public class JwtService {
//    private final String SECRET_KEY = "BF7FD11ACE545745B7BA1AF98B6F156D127BC7BB544BAB6A4FD74E4FC7";

    @Value("${project.jwtSecret}")
    private String SECRET_KEY;

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .signWith(getSignInKey())
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 720 * 60 * 1000))
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <Z> Z extractClaim(String token, Function<Claims, Z> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaim(token, (Claims claims) -> claims.getSubject());
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, (Claims claims) -> claims.getExpiration());
    }

    public Boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        return !isTokenExpired(token) && userDetails.getUsername().equals(extractEmail(token));
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
