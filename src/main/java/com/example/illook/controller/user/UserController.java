package com.example.illook.controller.user;

import com.example.illook.mapper.ImageMapper;
import com.example.illook.mapper.PostMapper;
import com.example.illook.mapper.UserMapper;
import com.example.illook.model.User;
import com.example.illook.util.ApiResponse;
import com.example.illook.payload.EmailRequest;
import com.example.illook.payload.SignUpRequest;
import com.example.illook.payload.TokenRequestDto;
import com.example.illook.service.MailService;
import com.example.illook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.example.illook.util.mybatisEmpty.empty;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final ImageMapper imageMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final MailService mailService;

    //아이디 중복확인
    @PostMapping("/user/check/id/duplication")
    public ApiResponse checkIdDuplication(@RequestParam(value = "id") String id) {

        if(id.isEmpty()){
            throw new IllegalStateException("아이디를 입력해주세요");
        }

        if (!empty(userMapper.checkIdDuplication(id))) {
            throw new IllegalStateException("사용중인 아이디입니다");
        }

        return ApiResponse.createSuccess("사용 가능한 아이디입니다");
    }

    //닉네임 중복확인
    @PostMapping("/user/check/nickname/duplication")
    public ApiResponse checkNicknameDuplication(@RequestParam String nickname) {

        if(nickname.isEmpty()){
            throw new IllegalStateException("닉네임을 입력해주세요");
        }

        if (!empty(userMapper.checkNicknameDuplicate(nickname))) {
            throw new IllegalStateException("사용중인 닉네임입니다");
        }

        return ApiResponse.createSuccess("사용 가능한 닉네임입니다");
    }

    //회원가입, 이메일 체크, 인증번호 전송
    @PostMapping("user/mailConfirm")
    public ApiResponse mailAuth(@Valid @RequestBody EmailRequest emailRequest, HttpServletRequest request){

        //이메일 중복 체크
        if(!empty(userMapper.checkEmailDuplicate(emailRequest.getEmail()))){
            throw new IllegalStateException("이미 가입된 이메일 입니다.");
        };

        //이메일로 인증번호 보내기
        mailService.mailSend(request.getSession(), emailRequest.getEmail());
        return ApiResponse.createSuccess("사용 가능한 이메일 입니다");
    }

    //3. 인증번호 일치여부
    @PostMapping("user/check/code")
    public ApiResponse checkAuth(HttpServletRequest request,@RequestBody EmailRequest emailRequest, @RequestParam("inputCode") String inputCode){
        HttpSession session = request.getSession();

        System.out.println(emailRequest);
        System.out.println(inputCode);
        System.out.println(session.getAttribute(emailRequest.getEmail()));
        System.out.println(session.getAttribute(inputCode));

        if(!session.getAttribute(emailRequest.getEmail()).equals(inputCode)){
            throw new IllegalStateException("인증번호가 일치하지 않습니다");
        };

        return ApiResponse.createSuccess("인증번호가 일치합니다");
    }



    //유저 회원가입
    @PostMapping("/user")
    public ApiResponse saveUser(@Valid @RequestBody SignUpRequest payload, BindingResult bindingResult) throws MethodArgumentNotValidException {

        //저장 단계에서는 아이디,닉네임 중복확인 안한다고 가정한다.

        if (!payload.getPassword().equals(payload.getPassword2())) {
            bindingResult.rejectValue("password2","range","비밀번호가 같지 않습니다");
        }
        if(bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

        //유저 회원가입
       userService.saveUser(payload);
       return ApiResponse.createSuccessWithNoContent();
    }


    // 로그인 하기 않은 사용자 : 내 프로필 없음, 다른 사용자 프로필 볼 수 있음 -> 분리시켜야 함!!
    //유저 profile 상세
    /*@GetMapping("/user/{userIdx}")
    public ApiResponse getUserProfile(@AuthenticationPrincipal User user, @PathVariable int userIdx){
        if(userIdx == -1){
            //내프로필
            List<Map> images = postMapper.getImage(Integer.parseInt(user.getUserIdx()));
            Map data = userMapper.getUserProfile(Integer.parseInt(user.getUserIdx()),Integer.parseInt(user.getUserIdx()));
            data.put("images",images);
            return ApiResponse.createSuccess(data);
        }else {
            //다른 사람 프로필
            List<Map> images = postMapper.getImage(userIdx);
            Map data = userMapper.getUserProfile(userIdx, Integer.parseInt(user.getUserIdx()));
            data.put("images", images);
            return ApiResponse.createSuccess(data);
        }
    }*/

    //[수정!!!!]
    //내 프로필
    @GetMapping("/user")
    public ApiResponse getMyProfile(@AuthenticationPrincipal User user){
        int userIdx = Integer.parseInt(user.getUserIdx());
        Map profile = userService.getProfile(userIdx,userIdx);
        return ApiResponse.createSuccess(profile);
    }
    //다른 사람 프로필
    @GetMapping("/user/{userIdx}")
    public ApiResponse getUserProfile(@AuthenticationPrincipal User user, @PathVariable int userIdx){
        Map profile = userService.getProfile(userIdx, Integer.parseInt(user.getUserIdx()));
        return ApiResponse.createSuccess(profile);
    }


    //유저 삭제
    @DeleteMapping("/user")
    public ApiResponse userDelete(@AuthenticationPrincipal User user){
        userMapper.deleteUser(Integer.parseInt(user.getUserIdx()));
        return ApiResponse.createSuccessWithNoContent();
    }

    //유저 수정
    @PatchMapping("/user")
    public ApiResponse userPatch(@AuthenticationPrincipal User user,
                                 @RequestParam(value = "file") List<MultipartFile> files,
                                 @RequestParam("nickname") String nickname) throws Exception{

        //닉네임 중복 확인
        if(!empty(userMapper.checkNicknameDuplicate(nickname))){
            throw new IllegalStateException("사용중인 닉네임입니다");
        }

        userService.updateUser(files, nickname, user);
        return ApiResponse.createSuccessWithNoContent();
    }

    // profile 게시물 사진
    @GetMapping("/user/post/image")
    public List<Map> getImages(@AuthenticationPrincipal User user){
        return postMapper.getImage(Integer.parseInt(user.getUserIdx()));
    }

    // 유저 프로필 사진
    @GetMapping("/user/image/{id}")
    public ApiResponse getImageProfile(@AuthenticationPrincipal User user, @PathVariable int id){
        return ApiResponse.createSuccess(imageMapper.getImageProfile(id));
    }

    //로그인
    @PostMapping("/user/login")
    public ApiResponse login(@RequestBody Map<String, String> user, HttpServletResponse response) {
        userService.login(user, response);
        return ApiResponse.createSuccessWithNoContent();
    }

    @PostMapping("/user/logout")
    public ApiResponse logout(@RequestBody TokenRequestDto tokenRequestDto) {
        userService.logout(tokenRequestDto);
        return ApiResponse.createSuccessWithNoContent();
    }

}

