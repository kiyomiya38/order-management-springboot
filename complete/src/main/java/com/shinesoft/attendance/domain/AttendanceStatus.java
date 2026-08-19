package com.shinesoft.attendance.domain;

public enum AttendanceStatus {
    NOT_STARTED("未出勤"),
    WORKING("出勤中"),
    FINISHED("退勤済み");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
