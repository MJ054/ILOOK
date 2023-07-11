package com.example.ilook.Model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    String comment;
    //Date createDate;
    int parent;
    //int userIdx;
    int postIdx;
}
