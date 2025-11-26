package com.hoidap.hoidapdemo.application.port;

import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.CreateLopRequest;

public interface LopServicePort {
    void createLop(CreateLopRequest request);
}
