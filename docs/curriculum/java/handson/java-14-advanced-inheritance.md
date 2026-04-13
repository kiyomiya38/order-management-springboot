# Java-14 ハンズオン: 高度な継承（abstract / interface）

対応参考資料: `Java-14_高度な継承.pptx`

## 1. この資料のゴール
- 抽象クラス（`abstract class`）の用途を説明できる
- インターフェース（`interface`）の用途を説明できる
- 実装クラスで共通仕様を満たす設計を実装できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. 抽象クラスは「共通処理 + 未実装メソッド」を持てる
2. インターフェースは「できることの約束」
3. `implements` したクラスは約束したメソッド実装が必須

---

## 4. ハンズオン

目的:
- 実務でよく使う抽象化設計を体験する

完了条件:
- 抽象クラスとインターフェースの両方を使ったサンプルを実行できる

作成ファイル: `~/order-management-springboot/practice/java/handson14/AdvancedInheritanceDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson14
cd ~/order-management-springboot/practice/java/handson14
```

### Step 1: 抽象クラスを使う
`AdvancedInheritanceDemo.java` を次の内容で作成:

```java
abstract class PaymentService {
    void printStart() {
        System.out.println("決済開始");
    }

    abstract int calculateFee(int amount); // 子クラスで実装必須
}

class CardPaymentService extends PaymentService {
    @Override
    int calculateFee(int amount) {
        return amount / 100; // 1%
    }
}

public class AdvancedInheritanceDemo {
    public static void main(String[] args) {
        CardPaymentService service = new CardPaymentService();
        service.printStart();
        System.out.println("手数料: " + service.calculateFee(5000));
    }
}
```

実行:
```bash
javac -encoding UTF-8 AdvancedInheritanceDemo.java
java AdvancedInheritanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: インターフェースを追加
`AdvancedInheritanceDemo.java` を次の内容に更新:

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

abstract class PaymentService {
    void printStart() {
        System.out.println("決済開始");
    }

    abstract int calculateFee(int amount);
}

class CardPaymentService extends PaymentService {
    @Override
    int calculateFee(int amount) {
        return amount / 100;
    }
}

public class AdvancedInheritanceDemo {
    public static void main(String[] args) {
        PaymentService service = new CardPaymentService();
        Notifier notifier = new ConsoleNotifier();

        int fee = service.calculateFee(5000);
        notifier.notifyResult("手数料: " + fee);
    }
}
```

実行:
```bash
javac -encoding UTF-8 AdvancedInheritanceDemo.java
java AdvancedInheritanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `BankPaymentService` を追加し、手数料計算を変更
2. `Notifier` 実装として `SimpleNotifier` を追加
3. 決済金額を変えて出力を比較

---

## 6. つまずきポイント
- `... is not abstract and does not override ...`
  -> `abstract`/`interface` の未実装メソッドを実装
- 抽象クラスを `new` しようとしてエラー
  -> 抽象クラスは直接インスタンス化できない
- `@Override` の付与漏れ
  -> 実装ミス防止のため付ける
