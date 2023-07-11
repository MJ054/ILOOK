package com.example.ilook.Model;

public class Register {

    String id;
    String email;
    String password;
    String password2;
    String nickname;

    public Register(String id, String email, String password, String password2, String nickname){
        this.id = id;
        this.email = email;
        this.password = password;
        this.password2 = password2;
        this.nickname = nickname;
    }

}
