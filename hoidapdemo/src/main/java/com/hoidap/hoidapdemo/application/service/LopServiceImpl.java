package com.hoidap.hoidapdemo.application.service;

import com.hoidap.hoidapdemo.application.port.LopServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.LopJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.CVHTJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.LopJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.CreateLopRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LopServiceImpl implements LopServicePort {
    private final LopJpaRepository lopRepo;
    private final CVHTJpaRepository cvhtRepo;

    public LopServiceImpl(LopJpaRepository lopRepo, CVHTJpaRepository cvhtRepo) {
        this.lopRepo = lopRepo;
        this.cvhtRepo= cvhtRepo;
    }

    @Override
    @Transactional
    public void createLop(CreateLopRequest request) {
        if (lopRepo.existsById(request.getMaLop())) {
            throw new IllegalArgumentException("Mã lớp đã tồn tại: " + request.getMaLop());
        }

        LopJpaEntity lop = new LopJpaEntity();
        lop.setMaLop(request.getMaLop());
        lop.setKhoaHoc(request.getKhoaHoc());
        lop.setChuyenNganh(request.getChuyenNganh());

        if (request.getMaCvht() != null && !request.getMaCvht().isEmpty()) {
            CVHTJpaEntity cvht = cvhtRepo.findById(request.getMaCvht())
                    .orElseThrow(() -> new IllegalArgumentException("Mã CVHT không tồn tại"));
            lop.setCvht(cvht);
        }

        lopRepo.save(lop);
    }
}
