package com.example.ilook.Model;

import android.graphics.Bitmap;

public class PickMain {

    Bitmap image;
    int date;
    Double postIdx;

    public void setPostIdx(Double postIdx) {
        this.postIdx = postIdx;
    }

    public Double getPostIdx() {
        return postIdx;
    }

    public PickMain(Bitmap image, int date, Double postIdx){
        this.image = image;
        this.date = date;
        this.postIdx = postIdx;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
