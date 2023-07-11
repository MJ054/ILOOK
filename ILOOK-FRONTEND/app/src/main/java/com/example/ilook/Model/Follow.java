package com.example.ilook.Model;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Follow {
    URL image;
    String nickname;
    int userIdx;
}
