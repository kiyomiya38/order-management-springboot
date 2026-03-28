// Step 1: 直書き版
// 特徴: メッセージ作成と表示(System.out.println)を同じクラスで行う。
class AttendanceMessageDirect {
    void notifyClockIn(String userName) {
        String message = "[APP] [INFO] " + userName + " さんが出勤しました";
        System.out.println(message);
    }

    void notifyClockOut(String userName) {
        String message = "[APP] [INFO] " + userName + " さんが退勤しました";
        System.out.println(message);
    }

    void notifyBreakStart(String userName) {
        String message = "[APP] [INFO] " + userName + " さんが休憩開始しました";
        System.out.println(message);
    }

    void notifyBreakEnd(String userName) {
        String message = "[APP] [INFO] " + userName + " さんが休憩終了しました";
        System.out.println(message);
    }
}

// Step 2: DI版
// 「表示する役割」を分離する。
interface MessagePrinter {
    void print(String message);
}

// implements MessagePrinter:
// MessagePrinterのルール（printメソッド）を実装する宣言。
class ConsolePrinter implements MessagePrinter {
    public void print(String message) {
        System.out.println("[APP] " + message);
    }
}

// メッセージ作成だけ担当。
// 表示方法は外から受け取る（手動DI）。
class AttendanceMessageWithDi {
    // private: 外部から直接変更不可
    // final: コンストラクタ設定後に再代入不可
    private final MessagePrinter printer;

    AttendanceMessageWithDi(MessagePrinter printer) {
        this.printer = printer;
    }

    void notifyClockIn(String userName) {
        String message = "[INFO] " + userName + " さんが出勤しました";
        printer.print(message);
    }

    void notifyClockOut(String userName) {
        String message = "[INFO] " + userName + " さんが退勤しました";
        printer.print(message);
    }

    void notifyBreakStart(String userName) {
        String message = "[INFO] " + userName + " さんが休憩開始しました";
        printer.print(message);
    }

    void notifyBreakEnd(String userName) {
        String message = "[INFO] " + userName + " さんが休憩終了しました";
        printer.print(message);
    }
}

public class Lesson3Main {
    public static void main(String[] args) {
        System.out.println("=== Step 1: 直書き版 ===");
        AttendanceMessageDirect direct = new AttendanceMessageDirect();
        direct.notifyClockIn("Yamada");
        direct.notifyClockOut("Yamada");
        direct.notifyBreakStart("Yamada");
        direct.notifyBreakEnd("Yamada");

        System.out.println("=== Step 2: DI版（Console出力） ===");
        MessagePrinter consolePrinter = new ConsolePrinter();
        AttendanceMessageWithDi withDi = new AttendanceMessageWithDi(consolePrinter);
        withDi.notifyClockIn("Yamada");
        withDi.notifyClockOut("Yamada");
        withDi.notifyBreakStart("Yamada");
        withDi.notifyBreakEnd("Yamada");
    }
}