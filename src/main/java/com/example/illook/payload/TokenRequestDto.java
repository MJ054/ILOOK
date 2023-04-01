package com.example.illook.payload;

import lombok.Data;

@Data
public class TokenRequestDto {
    String accessToken;
    String refreshToken;
}
