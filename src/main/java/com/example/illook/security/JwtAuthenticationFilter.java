package com.example.illook.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String accessToken = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest)request);

        checkToken(accessToken, refreshToken, (HttpServletResponse) response);

        chain.doFilter(request, response);
    }

    public void checkToken(String accessToken, String refreshToken, HttpServletResponse response){

        if(accessToken !=null  && !accessToken.equals("0")){
            if(jwtTokenProvider.validateToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else if(!jwtTokenProvider.validateToken(accessToken) && refreshToken != null){
                //accessToken 정보로 refreshToken 가져오는 함수
                boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);
                boolean isRefreshToken = jwtTokenProvider.validateToken(refreshToken);
                if(validateRefreshToken && isRefreshToken){
                    String userPk = jwtTokenProvider.getUserPk(refreshToken);
                    String role = jwtTokenProvider.getRoles(userPk);

                    String newAccessToken = jwtTokenProvider.createToken(userPk, role);
                    jwtTokenProvider.setHeaderAccessToken((HttpServletResponse) response, newAccessToken);

                    Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

    }
}
