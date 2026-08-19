# Java-14 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-14-advanced-inheritance.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（抽象クラス）解答
```java
class CashPaymentService extends PaymentService {
    @Override
    String getPaymentName() {
        return "現金";
    }
    @Override
    int calculateFee(int amount) {
        return 0;
    }
}
```

`main(...)`で`PaymentService cash = new CashPaymentService();`を生成し、`cash.pay(5000, consoleNotifier);`を呼ぶ。

## レベル2（インターフェース）解答
レベル1の`CashPaymentService`は残し、次の通知クラスを追加する。
```java
class ReceiptNotifier implements Notifier {
    @Override
    public void notifyResult(String message) {
        System.out.println("領収書: " + message);
    }
}
```

`Notifier notifier = new ReceiptNotifier();`を生成し、`card.pay(5000, notifier);`を呼ぶ。

## レベル3（抽象クラスの必要性を確認）解答
レベル2の完成コードから`CashPaymentService.calculateFee(...)`を削除すると、抽象メソッドの実装が不足するためコンパイルエラーになる。確認後はレベル1で追加した実装へ戻す。

一時的なエラー確認を元へ戻した後は、次のコードがレベル3完了状態です。

### レベル3完了時の全コード

```java
interface Notifier {
    void notifyResult(String message);
}

class ConsoleNotifier implements Notifier {
    @Override
    public void notifyResult(String message) {
        System.out.println("[通知] " + message);
    }
}

class SimpleNotifier implements Notifier {
    @Override
    public void notifyResult(String message) {
        System.out.println("通知: " + message);
    }
}

// ===== レベル2で追加: 領収書形式の通知 =====
class ReceiptNotifier implements Notifier {
    @Override
    public void notifyResult(String message) {
        System.out.println("領収書: " + message);
    }
}
// ===== レベル2で追加ここまで =====

abstract class PaymentService {
    void pay(int amount, Notifier notifier) {
        System.out.println("決済開始");

        String paymentName = getPaymentName();
        int fee = calculateFee(amount);
        int total = amount + fee;

        System.out.println("決済方法: " + paymentName);
        System.out.println("金額: " + amount);
        System.out.println("手数料: " + fee);
        System.out.println("合計: " + total);
        System.out.println("決済完了");
        notifier.notifyResult(paymentName + "決済が完了しました。合計: " + total);
    }

    abstract String getPaymentName();

    abstract int calculateFee(int amount);
}

class CardPaymentService extends PaymentService {
    @Override
    String getPaymentName() {
        return "カード";
    }

    @Override
    int calculateFee(int amount) {
        return amount / 100;
    }
}

class BankPaymentService extends PaymentService {
    @Override
    String getPaymentName() {
        return "銀行振込";
    }

    @Override
    int calculateFee(int amount) {
        return 300;
    }
}

// ===== レベル1で追加: 手数料0円の現金決済 =====
class CashPaymentService extends PaymentService {
    @Override
    String getPaymentName() {
        return "現金";
    }

    @Override
    int calculateFee(int amount) {
        return 0;
    }
}
// ===== レベル1で追加ここまで =====

public class AdvancedInheritanceDemo {
    public static void main(String[] args) {
        PaymentService card = new CardPaymentService();
        PaymentService bank = new BankPaymentService();
        PaymentService cash = new CashPaymentService();

        Notifier consoleNotifier = new ConsoleNotifier();
        Notifier simpleNotifier = new SimpleNotifier();

        card.pay(5000, consoleNotifier);
        System.out.println("---");
        bank.pay(5000, simpleNotifier);

        // ===== レベル1で追加: 現金決済を共通のpay(...)で実行 =====
        System.out.println("---");
        cash.pay(5000, consoleNotifier);
        // ===== レベル1で追加ここまで =====

        // ===== レベル2で追加: 通知方法だけReceiptNotifierへ差し替える =====
        Notifier notifier = new ReceiptNotifier();
        System.out.println("---");
        card.pay(5000, notifier);
        // ===== レベル2で追加ここまで =====
    }
}
```
