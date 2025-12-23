package com.hoidap.hoidapdemo.service.port;

import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import com.hoidap.hoidapdemo.dto.lop.CreateLopRequest;

import java.util.List;

public interface LopServicePort {
    void createLop(CreateLopRequest request);
    List<LopJpaEntity> getAllLop();
    LopJpaEntity getLopById(String id);
    void updateLop(String id, CreateLopRequest request);
    void deleteLop(String id);
    void saveListLop(List<LopJpaEntity> listLop);
}
