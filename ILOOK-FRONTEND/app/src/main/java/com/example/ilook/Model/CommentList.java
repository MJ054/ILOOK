package com.example.ilook.Model;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentList {
    int userIdx;
    int parent;
    URL profileImage;
    String nickname;
    String comment;
    String createDate;
}
