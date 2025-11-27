package com.hoidap.hoidapdemo.application.port;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.LopJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.CreateLopRequest;

import java.util.List;

public interface LopServicePort {
    void createLop(CreateLopRequest request);
    List<LopJpaEntity> getAllLop();
    LopJpaEntity getLopById(String id);
    void updateLop(String id, CreateLopRequest request);
    void deleteLop(String id);
    void saveListLop(List<LopJpaEntity> listLop);
}
