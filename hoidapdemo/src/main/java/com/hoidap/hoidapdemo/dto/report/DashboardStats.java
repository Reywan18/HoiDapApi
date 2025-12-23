package com.hoidap.hoidapdemo.dto.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardStats {
    private long totalQuestions;
    private long totalAnswered;
    private double resolutionRate;

    private List<AdvisorStat> advisorStats;
    private List<StudentStat> topStudents;
    private List<ClassStat> classStats;
}

