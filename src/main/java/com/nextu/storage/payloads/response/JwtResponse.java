package com.nextu.storage.payloads.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class JwtResponse {
    private final String token;
    private final String refreshToken;
    private final String id;
    private final String email;
    private final String firstName;
    private final List<String> roles;
    private static final String TYPE = "Bearer";
}
