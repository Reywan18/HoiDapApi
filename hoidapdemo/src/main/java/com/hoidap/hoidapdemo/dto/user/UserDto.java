package com.hoidap.hoidapdemo.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String maDinhDanh;
    private String hoTen;
    private String email;
    private String role;
}
