package com.example.illook.service;

import com.example.illook.mapper.PostMapper;
import com.example.illook.mapper.UserMapper;
import com.example.illook.model.Image;
import com.example.illook.payload.SignUpRequest;
import com.example.illook.payload.TokenRequestDto;
import com.example.illook.model.User;
import com.example.illook.security.JwtTokenProvider;
import com.example.illook.util.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final FileHandler fileHandler;
    private final PostMapper postMapper;


    @Transactional
    public void logout(TokenRequestDto tokenRequestDto){
        // 로그아웃 하고 싶은 토큰이 유효한 지 먼저 검증하기
        if (!jwtTokenProvider.validateToken(tokenRequestDto.getAccessToken())){
            throw new IllegalArgumentException("로그아웃 : 유효하지 않은 토큰입니다.");
        }

        // Access Token에서 User email을 가져온다
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // Redis에서 해당 User email로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
        if (redisTemplate.opsForValue().get("RT:"+authentication.getName())!=null){
            // Refresh Token을 삭제
            redisTemplate.delete("RT:"+authentication.getName());
        }

        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        Long expiration = jwtTokenProvider.getExpiration(tokenRequestDto.getAccessToken());
        redisTemplate.opsForValue().set(tokenRequestDto.getAccessToken(),"logout",expiration, TimeUnit.MILLISECONDS);

    }

    //유저 저장
    public void saveUser(SignUpRequest payload){
        String[] items = {"dog-g036b63d18_1920.jpg","girl-g996e72491_1920.jpg","tiktok-ga428cefdb_1920.jpg","woman-ga0cc40122_1920.jpg"};
        Random rand = new Random();

        User user = User.builder()
                .id(payload.getId())
                .email(payload.getEmail())
                .password(passwordEncoder.encode(payload.getPassword()))
                .nickname(payload.getNickname())
                .profileImage("images"+ File.separator +"basic"+ File.separator +items[rand.nextInt(4)])
                .role("ROLE_USER")
                .build();

        userMapper.saveUser(user);
    }

    //유저 수정
    // @Aspect 사용할라고 리턴 값 받음
    public void updateUser(List<MultipartFile> files, String nickname, User user) throws Exception {
        List<Image> imageList = fileHandler.parseFileInfo(files, null);
        userMapper.updateUser(nickname, imageList.get(0).getPath(), Integer.parseInt(user.getUserIdx()));

    }

    //로그인
    public void login(User user2, HttpServletResponse response){
        String accessToken = jwtTokenProvider.createToken(user2.getUserIdx(), user2.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user2.getUserIdx(), user2.getRole());
        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        userMapper.saveRefreshToken(refreshToken, user2.getId());
    }

    public Map getProfile(int userIdx, int userIdx2) {
        List<Map> images = postMapper.getImage(userIdx);
        Map data = userMapper.getUserProfile(userIdx,userIdx);
        data.put("images",images);

        List<Map> images = postMapper.getImage(userIdx);
        Map data = userMapper.getUserProfile(userIdx, Integer.parseInt(user.getUserIdx()));
        data.put("images", images);

        return data
    }
}
