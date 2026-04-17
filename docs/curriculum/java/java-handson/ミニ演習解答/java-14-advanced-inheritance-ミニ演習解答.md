# Java-14 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-14-advanced-inheritance.md`

## ミニ演習解答
```java
interface Notifier {
    void notifyResult(String message);
}

class SimpleNotifier implements Notifier {
    @Override
    public void notifyResult(String message) {
        System.out.println("[SIMPLE] " + message);
    }
}

abstract class PaymentService {
    abstract int calculateFee(int amount);
}

class CardPaymentService extends PaymentService {
    @Override
    int calculateFee(int amount) {
        return amount / 100; // 1%
    }
}

class BankPaymentService extends PaymentService {
    @Override
    int calculateFee(int amount) {
        return amount / 200; // 0.5%
    }
}

public class AdvancedInheritanceDemo {
    public static void main(String[] args) {
        Notifier notifier = new SimpleNotifier();
        PaymentService card = new CardPaymentService();
        PaymentService bank = new BankPaymentService();

        int amount1 = 5000;
        int amount2 = 12000;
        notifier.notifyResult("card fee(" + amount1 + ")=" + card.calculateFee(amount1));
        notifier.notifyResult("bank fee(" + amount1 + ")=" + bank.calculateFee(amount1));
        notifier.notifyResult("card fee(" + amount2 + ")=" + card.calculateFee(amount2));
        notifier.notifyResult("bank fee(" + amount2 + ")=" + bank.calculateFee(amount2));
    }
}
```
