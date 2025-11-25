package com.hoidap.hoidapdemo.application.port;

import com.hoidap.hoidapdemo.domain.model.UserRole;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.UserDto;

public interface UserServicePort {
    String register(String email, String password, String maDinhDanh, String hoTen, String soDienThoai,UserRole role);

    String login(String email, String password);

    UserDto getUserByEmail(String email);
}
