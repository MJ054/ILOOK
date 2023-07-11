package com.example.ilook.Model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import retrofit2.http.Url;

@Getter
@Setter
@AllArgsConstructor
public class DataPhoto2 {
    Uri image;
    int postIdx;
}
