// 勤怠ステータスを enum で表す。
// 取り得る値を限定できるのが enum のメリット。
enum AttendanceStatus {
    NOT_WORKING("未出勤"),
    LATE("遅刻"),
    WORKING("出勤中"),
    ON_BREAK("休憩中"),
    FINISHED("退勤済み");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    String getLabel() {
        return label;
    }
}

// 業務ルール違反を表す例外。
// 例: 未出勤で休憩開始しようとした、など。
class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

// 勤怠の業務ルールを持つサービスクラス。
class AttendanceService {
    private AttendanceStatus status = AttendanceStatus.NOT_WORKING;

    AttendanceStatus getStatus() {
        return status;
    }

    void clockIn() {
        if (status != AttendanceStatus.NOT_WORKING) {
            throw new BusinessException("出勤できるのは未出勤のときだけです");
        }
        status = AttendanceStatus.WORKING;
    }

    void startBreak() {
        if (status != AttendanceStatus.WORKING) {
            throw new BusinessException("休憩開始できるのは出勤中のときだけです");
        }
        status = AttendanceStatus.ON_BREAK;
    }

    void endBreak() {
        if (status != AttendanceStatus.ON_BREAK) {
            throw new BusinessException("休憩終了できるのは休憩中のときだけです");
        }
        status = AttendanceStatus.WORKING;
    }

    void clockOut() {
        if (status != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤できるのは出勤中のときだけです");
        }
        status = AttendanceStatus.FINISHED;
    }
}

public class Lesson4Main {
    public static void main(String[] args) {
        AttendanceService service = new AttendanceService();

        System.out.println("追加状態: " + AttendanceStatus.LATE.getLabel());

        System.out.println("初期状態: " + service.getStatus().getLabel());

        try {
            // 正常な流れ
            service.startBreak();
            service.clockIn();
            System.out.println("出勤後: " + service.getStatus().getLabel());

            service.startBreak();
            System.out.println("休憩開始後: " + service.getStatus().getLabel());

            service.endBreak();
            System.out.println("休憩終了後: " + service.getStatus().getLabel());

            service.clockOut();
            System.out.println("退勤後: " + service.getStatus().getLabel());

            // 異常な流れ（退勤後にもう一度退勤）
            service.clockOut();
        } catch (BusinessException e) {
            service.startBreak();
            System.out.println("[ERROR] " + e.getMessage());
        }
    }
}
