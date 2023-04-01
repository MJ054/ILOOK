package com.example.illook.security;

import com.example.illook.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String secretKey = "webfirewood";

    private long tokenValidTime = 30 * 60 * 1000L;
    private static final long refreshTokenValidMillisecond = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final CustomUserDetailService userDetailsService;
    private final UserMapper userProfileMapper;

    private final RedisTemplate redisTemplate;


    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String userPk, String roles){
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();

       return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // jwt refresh 토큰 생성
    public String createRefreshToken(String userPk, String roles) {

        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token){
        System.out.println(this.getUserPk(token));
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token'
    public String resolveAccessToken(HttpServletRequest request) {
        System.out.println(request.getHeader("authorization"));
        if(request.getHeader("authorization") != null )
            return request.getHeader("authorization");
        return null;
    }
    // Request의 Header에서 RefreshToken 값을 가져옵니다. "authorization" : "token'
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null )
            return request.getHeader("refreshToken");
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("authorization", "bearer "+ accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "bearer "+ refreshToken);
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        System.out.println(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoles(String userPK) {
        return userProfileMapper.findEmail(userPK);
    }


    // RefreshToken 존재유무 확인
    public boolean existsRefreshToken(String refreshToken) {
        return userProfileMapper.existRefreshToken(refreshToken);
    }

    // 만료된 accessToken 사용자 정보를 가져와 DB에서 refreshToken을 가져온다.
}
