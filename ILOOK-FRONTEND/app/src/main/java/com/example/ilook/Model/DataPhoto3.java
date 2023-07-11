package com.example.ilook.Model;

import android.graphics.Bitmap;

import java.net.URL;

public class DataPhoto3 {
    URL image;
    int postIdx;
    String category;

    public void setPostIdx(int postIdx) {
        this.postIdx = postIdx;
    }

    public int getPostIdx() {
        return postIdx;
    }

    public DataPhoto3(URL image, int postIdx, String category) {
        this.image = image;
        this.postIdx = postIdx;
        this.category = category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public DataPhoto3(URL image){
        this.image = image;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }

}
