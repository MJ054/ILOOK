package com.example.illook.controller;

import com.example.illook.mapper.ImageMapper;
import com.example.illook.mapper.PostMapper;
import com.example.illook.model.User;
import com.example.illook.payload.Response.ApiResponse;
import com.example.illook.payload.BoardRequestDto.BoardFileVo;
import com.example.illook.payload.BoardRequestDto.PickFileVo;
import com.example.illook.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final PostMapper mapper;
    private final ImageMapper imageMapper;

    //게시글 이미지 업로드
    // @RequestParam 어노테이션은 required = true가 디폴트
    @PostMapping("post/image")
    public ApiResponse uploadImages(
            @RequestParam(value = "file") List<MultipartFile> files,
            @RequestParam("key") String key
    ) throws Exception{

        postService.createImage(files, key);
        return ApiResponse.createSuccessWithNoContent();
    }

    //게시글(OOTD) 데이터 추가
    @PostMapping("post/ootd")
    public ApiResponse uploadData(@AuthenticationPrincipal User user, @RequestBody BoardFileVo boardFileVo){

        int createOotdId = postService.createData(boardFileVo, Integer.parseInt(user.getUserIdx()));
        return ApiResponse.createSuccess(createOotdId);
    }

    //게시글(PICK) 데이터 추가
    @PostMapping("post/pick")
    public ApiResponse uploadDataPick(@AuthenticationPrincipal User user, @RequestBody PickFileVo pickFileVo){

        int createPickId = postService.createDataPick(pickFileVo, Integer.parseInt(user.getUserIdx()));
        return ApiResponse.createSuccess(createPickId);
    }

    //게시글 상세
    @GetMapping("post/{id}")
    public ApiResponse<Map> getPost1(@AuthenticationPrincipal User user, @PathVariable int id) throws Exception{

        if(user == null){
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다");
        }

        Map postDetail = postService.getPostDetail(user, id);
        return ApiResponse.createSuccess(postDetail);

    }

    //게시글 삭제
    @DeleteMapping("post/{id}")
    public ApiResponse deletePost(@PathVariable int id) throws Exception{

        mapper.deletePost(id);
        return ApiResponse.createSuccessWithNoContent();
    }

    //좋아요
    @PostMapping("post/like")
    public ApiResponse postLikes(@AuthenticationPrincipal User user, @RequestParam int post){

        mapper.postLikes(Integer.parseInt(user.getUserIdx()), post);
        return ApiResponse.createSuccessWithNoContent();
    }
    //좋아요 취소
    @DeleteMapping("post/like")
    public ApiResponse postDeleteLike(@AuthenticationPrincipal User user, @RequestParam int post){

        mapper.postLikesDelete(Integer.parseInt(user.getUserIdx()), post);
        return ApiResponse.createSuccessWithNoContent();
    }


    //메인 페이지 사진
    @GetMapping("post/main/{category}")
    public ApiResponse getMainImages(@PathVariable String category, @RequestParam int offset, @RequestParam int limit){
        List<Map> map = imageMapper.getMainImages(category, offset, limit);
        if(map.isEmpty()) return null;
        return ApiResponse.createSuccess(imageMapper.getMainImages(category, offset, limit));
    }

}
