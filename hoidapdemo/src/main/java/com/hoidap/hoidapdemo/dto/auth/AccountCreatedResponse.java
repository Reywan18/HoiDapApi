package com.hoidap.hoidapdemo.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreatedResponse {
    private String email;
    private String generatedPassword;
    private String message;
}
