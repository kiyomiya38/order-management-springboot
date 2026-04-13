public class Attendance {
    String status = "NOT_STARTED"; // NOT_STARTED / WORKING / FINISHED

    void clockIn() {
        if ("NOT_STARTED".equals(status)) {
            status = "WORKING";
        } else {
            System.out.println("すでに出勤済みです");
        }
    }

    void clockOut() {
        if ("WORKING".equals(status)) {
            status = "FINISHED";
        } else if ("NOT_STARTED".equals(status)) {
            System.out.println("先に出勤してください");
        } else {
            System.out.println("すでに退勤済みです");
        }
    }
}