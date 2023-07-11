package com.example.ilook.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardFileVo {

    private int postIdx;
    private String content;
    private int advertise;
    private String category;
    private List<PostProduct> products;

}
