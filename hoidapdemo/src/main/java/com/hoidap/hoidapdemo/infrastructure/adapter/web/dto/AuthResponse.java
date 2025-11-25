package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private int status;
    private String message;
    private String token;
}
