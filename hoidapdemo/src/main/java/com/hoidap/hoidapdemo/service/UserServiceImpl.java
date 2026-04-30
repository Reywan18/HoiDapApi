package com.hoidap.hoidapdemo.service;

import com.hoidap.hoidapdemo.service.port.UserServicePort;
import com.hoidap.hoidapdemo.entity.enums.UserRole;
import com.hoidap.hoidapdemo.entity.admin.AdminJpaEntity;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.repository.admin.AdminJpaRepository;
import com.hoidap.hoidapdemo.repository.cvht.CVHTJpaRepository;
import com.hoidap.hoidapdemo.repository.lop.LopJpaRepository;
import com.hoidap.hoidapdemo.repository.sinhvien.SinhVienJpaRepository;
import com.hoidap.hoidapdemo.security.JwtUtils;
import com.hoidap.hoidapdemo.dto.user.ProfileUpdateRequest;
import com.hoidap.hoidapdemo.dto.user.UserDto;
import com.hoidap.hoidapdemo.dto.user.UserProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserServicePort {
    private final SinhVienJpaRepository sinhVienRepo;
    private final CVHTJpaRepository cvhtRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final LopJpaRepository lopRepo;
    private final AdminJpaRepository adminRepo;

    public UserServiceImpl(
            SinhVienJpaRepository sinhVienRepo,
            CVHTJpaRepository cvhtRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            LopJpaRepository lopRepo,
            AdminJpaRepository adminRepo) {
        this.sinhVienRepo = sinhVienRepo;
        this.cvhtRepo = cvhtRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.lopRepo = lopRepo;
        this.adminRepo = adminRepo;
    }

    // @Override
    // @Transactional
    // public String register(String email, String password, String hoTen, String
    // soDienThoai,UserRole role) {
    // if (sinhVienRepo.findByEmail(email).isPresent() ||
    // cvhtRepo.findByEmail(email).isPresent()) {
    // throw new IllegalArgumentException("Email already exists");
    // }
    //
    // String hashedPassword = passwordEncoder.encode(password);
    // String generatedId;
    //
    // if (role == UserRole.SINH_VIEN) {
    // String maxSvId = sinhVienRepo.findMaxMaSv();
    // generatedId = generateNextId(maxSvId, "a");
    //
    // SinhVienJpaEntity sv = new SinhVienJpaEntity();
    // sv.setMaSv(generatedId);
    // sv.setEmail(email);
    // sv.setPassword(hashedPassword);
    // sv.setHoTen(hoTen);
    // sv.setSoDienThoai(soDienThoai);
    //
    // sinhVienRepo.save(sv);
    // return sv.getMaSv();
    //
    // } else if (role == UserRole.CVHT) {
    // String maxCvId = cvhtRepo.findMaxMaCv();
    // generatedId = generateNextId(maxCvId, "b");
    //
    // CVHTJpaEntity cv = new CVHTJpaEntity();
    // cv.setMaCv(generatedId);
    // cv.setEmail(email);
    // cv.setPassword(hashedPassword);
    // cv.setHoTen(hoTen);
    // cv.setSoDienThoai(soDienThoai);
    // cv.setChuyenMon(null);
    //
    // cvhtRepo.save(cv);
    // return cv.getMaCv();
    // }
    //
    // throw new IllegalArgumentException("Invalid user role specified: " +
    // role.name());
    // }

    @Override
    public String login(String email, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDto userDetails = this.getUserByEmail(email);

        return jwtUtils.generateJwtToken(
                authentication,
                userDetails.getMaDinhDanh(),
                userDetails.getHoTen(),
                userDetails.getRole());
    }

    @Override
    @Transactional
    public UserProfileResponse getMyProfile(String email) {
        var svOpt = sinhVienRepo.findByEmail(email);
        if (svOpt.isPresent()) {
            SinhVienJpaEntity sv = svOpt.get();

            String maLop = "Chưa có lớp";
            String tenLop = "";
            String maCoVan = null;
            String tenCoVan = null;

            if (sv.getLop() != null) {
                maLop = sv.getLop().getMaLop();
                tenLop = "Lớp " + sv.getLop().getMaLop();

                var cvhtCuaLop = sv.getLop().getCvht();

                if (cvhtCuaLop != null) {
                    maCoVan = cvhtCuaLop.getMaCv();
                    tenCoVan = cvhtCuaLop.getHoTen();
                }
            }

            return UserProfileResponse.builder()
                    .maDinhDanh(sv.getMaSv())
                    .hoTen(sv.getHoTen())
                    .email(sv.getEmail())
                    .soDienThoai(sv.getSoDienThoai())
                    .role("SINH_VIEN")
                    .maLop(sv.getLop() != null ? sv.getLop().getMaLop() : "Chưa có lớp")
                    .tenLop(sv.getLop() != null ? "Lớp " + sv.getLop().getMaLop() : "")
                    .maCoVan(maCoVan)
                    .tenCoVan(tenCoVan)
                    .build();
        }

        var cvhtOpt = cvhtRepo.findByEmail(email);
        if (cvhtOpt.isPresent()) {
            CVHTJpaEntity cv = cvhtOpt.get();
            
            // Lấy danh sách lớp trực tiếp từ Repo để tránh lỗi Lazy Loading hoặc Mapping
            List<LopJpaEntity> classes = lopRepo.findByCvhtId(cv.getMaCv());
            List<String> managedClassList = classes.stream()
                    .map(LopJpaEntity::getMaLop)
                    .toList();

            return UserProfileResponse.builder()
                    .maDinhDanh(cv.getMaCv())
                    .hoTen(cv.getHoTen())
                    .email(cv.getEmail())
                    .soDienThoai(cv.getSoDienThoai())
                    .role("CVHT")
                    .managedClasses(managedClassList)
                    .build();
        }
        throw new IllegalArgumentException("Không tìm thấy thông tin người dùng: " + email);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        var svOpt = sinhVienRepo.findByEmail(email);
        if (svOpt.isPresent()) {
            SinhVienJpaEntity sv = svOpt.get();
            return UserDto.builder()
                    .maDinhDanh(sv.getMaSv())
                    .hoTen(sv.getHoTen())
                    .email(sv.getEmail())
                    .role(UserRole.SINH_VIEN.name())
                    .build();
        }

        var cvhtOpt = cvhtRepo.findByEmail(email);
        if (cvhtOpt.isPresent()) {
            CVHTJpaEntity cv = cvhtOpt.get();
            return UserDto.builder()
                    .maDinhDanh(cv.getMaCv())
                    .hoTen(cv.getHoTen())
                    .email(cv.getEmail())
                    .role(UserRole.CVHT.name())
                    .build();
        }

        var adminOpt = adminRepo.findByEmail(email);
        if (adminOpt.isPresent()) {
            AdminJpaEntity admin = adminOpt.get();
            return UserDto.builder()
                    .maDinhDanh(admin.getEmail())
                    .hoTen(admin.getHoTen())
                    .email(null)
                    .role(UserRole.ADMIN.name())
                    .build();
        }

        throw new IllegalArgumentException("User not found with identifier: " + email);
    }

    private String generateNextId(String maxId, String prefix) {
        if (maxId == null || maxId.length() < 6) {
            return prefix + "00000";
        }

        String numberPart = maxId.substring(1);
        int number = Integer.parseInt(numberPart);
        int nextNumber = number + 1;

        String nextNumberPart = String.format("%05d", nextNumber);

        return prefix + nextNumberPart;
    }

    @Override
    @Transactional
    public void updateProfile(String email, ProfileUpdateRequest request) {
        var svOpt = sinhVienRepo.findByEmail(email);
        if (svOpt.isPresent()) {
            SinhVienJpaEntity sv = svOpt.get();

            if (request.getHoTen() != null)
                sv.setHoTen(request.getHoTen());
            if (request.getSoDienThoai() != null)
                sv.setSoDienThoai(request.getSoDienThoai());

            if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
                sv.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }

            if (request.getMaLop() != null) {
                LopJpaEntity lop = lopRepo.findById(request.getMaLop())
                        .orElseThrow(() -> new IllegalArgumentException("Mã lớp không tồn tại: " + request.getMaLop()));
                sv.setLop(lop);
            }

            sinhVienRepo.save(sv);
            return;
        }

        var cvhtOpt = cvhtRepo.findByEmail(email);
        if (cvhtOpt.isPresent()) {
            CVHTJpaEntity cv = cvhtOpt.get();

            if (request.getHoTen() != null)
                cv.setHoTen(request.getHoTen());
            if (request.getSoDienThoai() != null)
                cv.setSoDienThoai(request.getSoDienThoai());

            if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
                cv.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }

            if (request.getChuyenMon() != null) {
                cv.setChuyenMon(request.getChuyenMon());
            }

            cvhtRepo.save(cv);
            return;
        }

        throw new IllegalArgumentException("Không tìm thấy user với email: " + email);
    }

    // Admin SinhVien
    @Override
    public List<SinhVienJpaEntity> getAllSinhVien() {
        return sinhVienRepo.findAll();
    }

    @Override
    public org.springframework.data.domain.Page<SinhVienJpaEntity> getAllSinhVien(String keyword,
            org.springframework.data.domain.Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return sinhVienRepo.searchSinhVien(keyword.trim(), pageable);
        }
        return sinhVienRepo.findAll(pageable);
    }

    @Override
    public SinhVienJpaEntity getSinhVienById(String id) {
        return sinhVienRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy SV: " + id));
    }

    @Override
    @Transactional
    public void saveSinhVien(SinhVienJpaEntity sv) {
        if (sinhVienRepo.existsById(sv.getMaSv())) {
            SinhVienJpaEntity oldSV = getSinhVienById(sv.getMaSv());
            // Giu lai cac truong khong duoc gui len tu frontend
            if (sv.getEmail() == null || sv.getEmail().isEmpty()) {
                sv.setEmail(oldSV.getEmail());
            }
            if (sv.getRole() == null || sv.getRole().isEmpty()) {
                sv.setRole(oldSV.getRole());
            }
            if (sv.getHoTen() == null || sv.getHoTen().isEmpty()) {
                sv.setHoTen(oldSV.getHoTen());
            }
            // Xu ly password
            if (sv.getPassword() == null || sv.getPassword().isEmpty()) {
                sv.setPassword(oldSV.getPassword());
            } else {
                sv.setPassword(passwordEncoder.encode(sv.getPassword()));
            }
        } else {
            sv.setPassword(passwordEncoder.encode(sv.getPassword()));
        }
        sinhVienRepo.save(sv);
    }

    @Override
    @Transactional
    public void deleteSinhVien(String id) {
        sinhVienRepo.deleteById(id);
    }

    // Admin Cvht
    @Override
    public List<CVHTJpaEntity> getAllCVHT() {
        return cvhtRepo.findAll();
    }

    @Override
    public org.springframework.data.domain.Page<CVHTJpaEntity> getAllCVHT(String keyword, org.springframework.data.domain.Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return cvhtRepo.searchCVHT(keyword.trim(), pageable);
        }
        return cvhtRepo.findAll(pageable);
    }

    @Override
    public CVHTJpaEntity getCVHTById(String id) {
        return cvhtRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Override
    @Transactional
    public void saveCVHT(CVHTJpaEntity cv) {
        if (cvhtRepo.existsById(cv.getMaCv())) {
            CVHTJpaEntity oldCV = getCVHTById(cv.getMaCv());
            // Giu lai cac truong khong duoc gui len tu frontend
            if (cv.getEmail() == null || cv.getEmail().isEmpty()) {
                cv.setEmail(oldCV.getEmail());
            }
            if (cv.getRole() == null || cv.getRole().isEmpty()) {
                cv.setRole(oldCV.getRole());
            }
            if (cv.getHoTen() == null || cv.getHoTen().isEmpty()) {
                cv.setHoTen(oldCV.getHoTen());
            }
            if (cv.getSoDienThoai() == null) {
                cv.setSoDienThoai(oldCV.getSoDienThoai());
            }
            // Xu ly password
            if (cv.getPassword() == null || cv.getPassword().isEmpty()) {
                cv.setPassword(oldCV.getPassword());
            } else {
                cv.setPassword(passwordEncoder.encode(cv.getPassword()));
            }
        } else {
            cv.setPassword(passwordEncoder.encode(cv.getPassword()));
        }
        cvhtRepo.save(cv);
    }

    @Override
    @Transactional
    public void deleteCVHT(String id) {
        cvhtRepo.deleteById(id);
    }

    // @Transactional
    // public void changePassword(String email, ChangePasswordRequest req) {
    // // Logic:
    // // 1. Tìm user theo email
    // // 2. Check passwordEncoder.matches(req.getOldPassword(), user.getPassword())
    // // 3. Nếu đúng ->
    // user.setPassword(passwordEncoder.encode(req.getNewPassword()))
    // // 4. Save
    // }
}
