package com.example.ilook.Model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentGroup {
    CommentList comment;
    List<CommentList> commentLists;
}
