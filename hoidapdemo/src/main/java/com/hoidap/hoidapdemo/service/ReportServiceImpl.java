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
        List<Object[]> advisorRaw = conversationRepo.findCVHTPerformance();
        List<AdvisorStat> advisorStats = advisorRaw.stream().map(obj -> {
            Double avgTime = obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0;
            Long advAnswered = obj[2] != null ? ((Number) obj[2]).longValue() : 0L;
            Long advTotal = obj[3] != null ? ((Number) obj[3]).longValue() : 0L;
            
            // Công thức tính hiệu suất (%): (Đã xử lý / Tổng được phân công) * 100
            // Nếu thời gian phản hồi quá chậm (> 48h), trừ thêm điểm hiệu suất (ví dụ: -10%)
            double efficiency = advTotal == 0 ? 0.0 : ((double) advAnswered / advTotal) * 100;
            if (avgTime > 48 && efficiency > 10) efficiency -= 10;
            
            return AdvisorStat.builder()
                    .name((String) obj[0])
                    .avgResponseTimeHours(avgTime)
                    .answeredCount(advAnswered)
                    .totalQuestions(advTotal)
                    .efficiencyPercentage(Math.round(efficiency * 10.0) / 10.0)
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
                .pendingQuestions(total - answered)
                .studentsCount(conversationRepo.countDistinctStudents())
                .resolutionRate(Math.round(rate * 100.0) / 100.0)
                .topStudents(topStudents)
                .advisorStats(advisorStats)
                .classStats(classStats)
                .weeklyTrend(java.util.Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L))
                .build();
    }

    public DashboardStats getDashboardStatsForAdvisor(String maCv) {
        long total = conversationRepo.countByCvht_MaCv(maCv);
        long answered = conversationRepo.countByCvht_MaCvAndTrangThai(maCv, ConversationStatus.RESOLVED);
        long waiting = conversationRepo.countByCvht_MaCvAndTrangThai(maCv, ConversationStatus.WAITING_FOR_CVHT);
        long chatting = conversationRepo.countByCvht_MaCvAndTrangThai(maCv, ConversationStatus.CHATTING_WITH_CVHT);
        long reported = conversationRepo.countByCvht_MaCvAndTrangThai(maCv, ConversationStatus.REPORTED);
        
        long studentsCount = conversationRepo.countDistinctStudentsByAdvisor(maCv);
        
        // Weekly Trend
        List<Object[]> weeklyTrendRaw = conversationRepo.findWeeklyTrendByAdvisor(maCv);
        Long[] weeklyTrend = new Long[7];
        for(int i=0; i<7; i++) weeklyTrend[i] = 0L;
        
        for (Object[] obj : weeklyTrendRaw) {
            int dayOfWeek = ((Number) obj[0]).intValue(); // 1 (Sun) to 7 (Sat)
            int index = (dayOfWeek + 5) % 7; // Chuyển sang 0 (Mon) to 6 (Sun)
            weeklyTrend[index] = ((Number) obj[1]).longValue();
        }

        return DashboardStats.builder()
                .totalQuestions(total)
                .totalAnswered(answered)
                .pendingQuestions(waiting + chatting + reported)
                .studentsCount(studentsCount)
                .resolutionRate(total == 0 ? 0 : Math.round(((double) answered / total) * 100.0 * 10.0) / 10.0)
                .weeklyTrend(java.util.Arrays.asList(weeklyTrend))
                .build();
    }
}
