public enum AttendanceStatus {
    NOT_CLOCKED_IN("未出勤"),
    WORKING("出勤中"),
    CLOCKED_OUT("退勤済");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}