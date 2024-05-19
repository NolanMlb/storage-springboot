package com.nextu.storage.payloads;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}
