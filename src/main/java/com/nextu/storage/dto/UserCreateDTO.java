package com.nextu.storage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class UserCreateDTO  extends UserDTO{
    private String password;
}
