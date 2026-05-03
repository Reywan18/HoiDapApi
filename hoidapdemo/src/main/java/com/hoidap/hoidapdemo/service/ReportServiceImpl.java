package com.hoidap.hoidapdemo.service;

import com.hoidap.hoidapdemo.entity.enums.ConversationStatus;
import com.hoidap.hoidapdemo.repository.chat.ConversationJpaRepository;
import com.hoidap.hoidapdemo.dto.report.AdvisorStat;
import com.hoidap.hoidapdemo.dto.report.ClassStat;
import com.hoidap.hoidapdemo.dto.report.DashboardStats;
// import com.hoidap.hoidapdemo.dto.report.StudentStat;
// import org.springframework.data.domain.PageRequest;
import com.hoidap.hoidapdemo.service.port.ReportServicePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportServicePort {
    private final ConversationJpaRepository conversationRepo;

    public ReportServiceImpl(ConversationJpaRepository conversationRepo) {
        this.conversationRepo = conversationRepo;
    }

    public DashboardStats getDashboardStats() {
        long total = conversationRepo.count();
        long answered = conversationRepo.countByTrangThai(ConversationStatus.RESOLVED);
        double rate = total == 0 ? 0 : ((double) answered / total) * 100;

        // Lấy Top 5 sinh viên
        List<Object[]> topStudentsRaw = conversationRepo.findTopStudents(org.springframework.data.domain.PageRequest.of(0, 5));
        List<com.hoidap.hoidapdemo.dto.report.StudentStat> topStudents = topStudentsRaw.stream().map(obj ->
            com.hoidap.hoidapdemo.dto.report.StudentStat.builder()
                .name((String) obj[0])
                .maSv((String) obj[1])
                .questionCount((Long) obj[2])
                .build()
        ).toList();

        // Lấy hiệu suất CVHT
        List<Object[]> advisorRaw = conversationRepo.findAdvisorPerformance();
        List<AdvisorStat> advisorStats = advisorRaw.stream().map(obj -> {
            Double avgTime = 0.0;
            if (obj[1] != null) {
                avgTime = ((Number) obj[1]).doubleValue();
            }
            return AdvisorStat.builder()
                    .name((String) obj[0])
                    .avgResponseTimeHours(avgTime)
                    .build();
        }).toList();

        List<Object[]> classRaw = conversationRepo.countQuestionsByClass();
        List<ClassStat> classStats = classRaw.stream().map(obj -> ClassStat.builder()
                .maLop((String) obj[0])
                .chuyenNganh((String) obj[1])
                .questionCount((Long) obj[2])
                .build()).toList();

        return DashboardStats.builder()
                .totalQuestions(total)
                .totalAnswered(answered)
                .resolutionRate(Math.round(rate * 100.0) / 100.0)
                .topStudents(topStudents)
                .advisorStats(advisorStats)
                .classStats(classStats)
                .build();
    }
}
