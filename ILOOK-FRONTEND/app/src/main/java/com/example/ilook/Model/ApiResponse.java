package com.example.ilook.Model;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

public class ApiResponse<T> {

    public String status;
    public T data;
    public String message;

    public ApiResponse(String status, T data, String message){
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public String getStatus(){return status;}
    public T getData(){
        return data;
    }
    public String getMessage(){return message;}

}
