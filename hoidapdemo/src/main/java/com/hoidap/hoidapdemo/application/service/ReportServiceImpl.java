package com.hoidap.hoidapdemo.application.service;

import com.hoidap.hoidapdemo.domain.model.QuestionStatus;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.question.QuestionJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.report.AdvisorStat;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.report.ClassStat;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.report.DashboardStats;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.report.StudentStat;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl {
    private final QuestionJpaRepository questionRepo;

    public ReportServiceImpl(QuestionJpaRepository questionRepo) {
        this.questionRepo = questionRepo;
    }

    public DashboardStats getDashboardStats() {
        long total = questionRepo.count();
        long answered = questionRepo.countByTrangThai(QuestionStatus.ANSWER);
        double rate = total == 0 ? 0 : ((double) answered / total) * 100;

        // Lấy Top 5 sinh viên
        List<Object[]> topStudentsRaw = questionRepo.findTopStudents(PageRequest.of(0, 5));
        List<StudentStat> topStudents = topStudentsRaw.stream().map(obj ->
                StudentStat.builder()
                        .name((String) obj[0])
                        .maSv((String) obj[1])
                        .questionCount((Long) obj[2])
                        .build()
        ).toList();

        // Lấy hiệu suất CVHT
        List<Object[]> advisorRaw = questionRepo.findAdvisorPerformance();
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

        List<Object[]> classRaw = questionRepo.countQuestionsByClass();
        List<ClassStat> classStats = classRaw.stream().map(obj ->
                ClassStat.builder()
                        .maLop((String) obj[0])
                        .chuyenNganh((String) obj[1])
                        .questionCount((Long) obj[2])
                        .build()
        ).toList();

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
