package com.hoidap.hoidapdemo.service.port;

import com.hoidap.hoidapdemo.dto.auth.AccountCreatedResponse;
import com.hoidap.hoidapdemo.dto.auth.CreateUserRequest;

public interface AdminServicePort {
    AccountCreatedResponse createStudentAccount(CreateUserRequest request);
    AccountCreatedResponse createAdvisorAccount(CreateUserRequest request);
}
