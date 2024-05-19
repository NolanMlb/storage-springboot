package com.nextu.storage.payloads;

import lombok.Data;

@Data
public class SignupRequest {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
}
