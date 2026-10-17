package com.example.employeemanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DashboardStatsResponse {

    private long totalEmployees;
    private long totalDepartments;
    private long presentToday;
    private long onLeave;
    private double attendanceRate;
    private double leavePercentage;
    private List<DepartmentStatDto> departmentOverview;
    private List<EmployeeResponse> recentEmployees;

    @Builder
    public DashboardStatsResponse(long totalEmployees, long totalDepartments, long presentToday,
                                  long onLeave, double attendanceRate, double leavePercentage,
                                  List<DepartmentStatDto> departmentOverview,
                                  List<EmployeeResponse> recentEmployees) {
        this.totalEmployees = totalEmployees;
        this.totalDepartments = totalDepartments;
        this.presentToday = presentToday;
        this.onLeave = onLeave;
        this.attendanceRate = attendanceRate;
        this.leavePercentage = leavePercentage;
        this.departmentOverview = departmentOverview;
        this.recentEmployees = recentEmployees;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DepartmentStatDto {
        private String name;
        private int count;
        private String icon;

        @Builder
        public DepartmentStatDto(String name, int count, String icon) {
            this.name = name;
            this.count = count;
            this.icon = icon;
        }
    }
}
