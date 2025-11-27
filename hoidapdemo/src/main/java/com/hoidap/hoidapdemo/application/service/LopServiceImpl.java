package com.hoidap.hoidapdemo.application.service;

import com.hoidap.hoidapdemo.application.port.LopServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.LopJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.CVHTJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.LopJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.CreateLopRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<LopJpaEntity> getAllLop() {
        return lopRepo.findAll();
    }

    @Override
    public LopJpaEntity getLopById(String id) {
        return lopRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lớp không tồn tại với mã: " + id));
    }

    @Override
    @Transactional
    public void updateLop(String id, CreateLopRequest request) {
        LopJpaEntity lop = getLopById(id);
        lop.setKhoaHoc(request.getKhoaHoc());
        lop.setChuyenNganh(request.getChuyenNganh());

        if (request.getMaCvht() != null && !request.getMaCvht().isEmpty()) {
            CVHTJpaEntity cvht = cvhtRepo.findById(request.getMaCvht())
                    .orElseThrow(() -> new IllegalArgumentException("Mã CVHT không tồn tại"));
            lop.setCvht(cvht);
        } else {
            lop.setCvht(null);
        }

        lopRepo.save(lop);
    }

    @Override
    @Transactional
    public void deleteLop(String id) {
        if (!lopRepo.existsById(id)) {
            throw new IllegalArgumentException("Không thể xóa. Lớp không tồn tại: " + id);
        }
        lopRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void saveListLop(List<LopJpaEntity> listLop) {
        lopRepo.saveAll(listLop);
    }
}
