package com.example.ilook.Api;

import com.example.ilook.Model.AccessToken;
import com.example.ilook.Model.BoardFileVo;
import com.example.ilook.Model.PickFileVo;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Model.Comment;
import com.example.ilook.Model.Email;
import com.example.ilook.Model.PasswordRequest;
import com.example.ilook.Model.Register;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {



    /**
     * PostController
     */

    @Multipart
    @POST("/post/image")
    Call<ApiResponse> uploadImages(@Part List<MultipartBody.Part> file,
                                   @Part("key") RequestBody param);

    @POST("/post/ootd")
    Call<ApiResponse> uploadData(@Body BoardFileVo boardFileVo);

    @POST("/post/pick")
    Call<ApiResponse> uploadDataPick(@Body PickFileVo pickFileVo);

    @GET("post/{id}")
    Call<ApiResponse> getPost1(@Path("id") int id);

    @DELETE("post/{id}")
    Call<ApiResponse> deletePost(@Path("id") int id);

    @POST("/post/like")
    Call<ApiResponse> postLikes(@Query("post") int post);

    @DELETE("post/like")
    Call<ApiResponse> postDeleteLike(@Query("post") int post);

    @GET("/post/main/{category}")
    Call<ApiResponse> getMainImages(@Path("category") String category,
                                    @Query("offset") int offset,
                                    @Query("limit") int limit);


    /**
     * FollowController
     */

    @POST("/follow")
    Call<ApiResponse> follow(@Query("follower") int follower);

    @DELETE("/follow")
    Call<ApiResponse> followDelete(@Query("follower") int follower);

    @GET("follow/list/{id}")
    Call<ApiResponse> followList(@Path("id") int id);

    @GET("following/list/{id}")
    Call<ApiResponse> followingList(@Path("id") int id);

    /**
     * userController
     */

    //아이디 체크
    @POST("user/check/id/duplication")
    Call<ApiResponse> checkIdDuplication(@Query("id") String id);

    //닉네임 체크
    @POST("/user/check/nickname/duplication")
    Call<ApiResponse> checkNicknameDuplication(@Query("nickname") String nickname);

    //가입하기
    @POST("/user")
    Call<ApiResponse> saveUser(@Body Register register);

    @POST("/user/login")
    Call<ApiResponse> login(@Body Map<String, String> user);

    @DELETE("/user")
    Call<ApiResponse> userDelete();

    @Multipart
    @PATCH("/user")
    Call<ApiResponse> userPatch(@Part List<MultipartBody.Part> file,
                                   @Part("nickname") RequestBody nickname);

    @POST("/user/loginAccessToken")
    Call<ApiResponse> loginAccessToken(@Body AccessToken accessToken);

    //
    @GET("/user/{userIdx}")
    Call<ApiResponse> getUserProfile(@Path("userIdx") int userIdx);

    @GET("/user/me/me")
    Call<ApiResponse> getMyProfile();

    @POST("user/mailConfirm")
    Call<ApiResponse> mailAuth(@Body Email email);

    @POST("user/check/code")
    Call<ApiResponse> checkAuth(
            @Body Email email,
            @Query("inputCode") String inputCode);

    @POST("/user/reissue")
    Call<ApiResponse> reissue();

    /**
         * UserFindController
         */

    //비밀번호 check
    //비밀번호 코드
    @POST("/user/help/pwInquiry")
    Call<ApiResponse> findPwd(@Body Email email, @Query("id") String id, @Query("inputCode") String inputCode);

    //비밀번호 변경
    @PATCH("/user/help/reset/password/{id}")
    Call<ApiResponse> changePwd(@Path("id") String id, @Body PasswordRequest passwordRequest);

    @POST("/user/help/mailConfirm")
    Call<ApiResponse> find(@Body Email email);

    @POST("/user/help/idInquiry")
    Call<ApiResponse> checkCode(@Body Email email, @Query("inputCode") String inputCode);


    /**
     * commentController
     */

    // 댓글(대댓글) 달기
    @POST("comment")
    Call<ApiResponse> comment(@Body Comment comment);

    // 댓글(대댓글) 리스트
    @GET("comment/list/{postIdx}/{parent}")
    Call<ApiResponse> commentList(@Path("postIdx") int postIdx, @Path("parent") int parent);


    @DELETE("comment/{id}")
    Call<ApiResponse> commentDelete(@Path("id") int id);

    @GET("user/search")
    Call<ApiResponse> getSearch(@Query("search") String search);

}