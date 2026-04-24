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

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- 例: `17.0.x`

---

## 3. 先に覚えるポイント
1. 抽象クラスは「共通の土台クラス」: 共通処理は親にまとめ、差分は抽象メソッドにする
2. 抽象クラスは直接 `new` できない。継承した子クラスを `new` して使う
3. 抽象メソッド（本体なしメソッド）は、子クラスに「この処理を必ず実装する」ことを強制できる
4. 使う目的は「重複を減らす + 実装漏れを防ぐ」
5. インターフェースは「できることの約束」。`implements` したクラスは約束したメソッド実装が必須

抽象クラスのイメージ（この章の例）:
- `PaymentService` は共通処理 `printStart()` を持つ
- 手数料計算 `calculateFee(...)` は決済手段ごとに異なるため、子クラスで実装する

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
abstract class PaymentService { // 抽象クラス: 共通処理と未実装メソッドを持つ
    void printStart() { // 共通処理（全決済で使う）
        System.out.println("決済開始");
    }

    abstract int calculateFee(int amount); // 抽象メソッド: 子クラスで実装必須
}

class CardPaymentService extends PaymentService { // 具体的な決済サービス実装
    @Override
    int calculateFee(int amount) { // 抽象メソッドを実装
        return amount / 100; // 金額の1%を手数料とする
    }
}

public class AdvancedInheritanceDemo { // 実行クラス
    public static void main(String[] args) {
        CardPaymentService service = new CardPaymentService(); // 具象クラスを生成
        service.printStart(); // 抽象クラスで定義した共通処理を呼ぶ
        System.out.println("手数料: " + service.calculateFee(5000)); // 子クラス実装の計算結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 AdvancedInheritanceDemo.java
java AdvancedInheritanceDemo
```

期待出力例:
```text
決済開始
手数料: 50
```



### Step 2: インターフェースを追加
`AdvancedInheritanceDemo.java` を次の内容に更新:

```java
interface Notifier { // 通知機能の仕様（できることの約束）
    void notifyResult(String message); // 通知メッセージ送信メソッド
}

class ConsoleNotifier implements Notifier { // インターフェース実装クラス
    @Override
    public void notifyResult(String message) { // 約束したメソッドを実装
        System.out.println("[通知] " + message); // コンソールへ通知表示
    }
}

abstract class PaymentService { // 抽象クラス
    void printStart() { // 共通処理
        System.out.println("決済開始");
    }

    abstract int calculateFee(int amount); // 子クラスで実装する抽象メソッド
}

class CardPaymentService extends PaymentService { // 具体的な決済実装
    @Override
    int calculateFee(int amount) {
        return amount / 100; // 1% 手数料
    }
}

public class AdvancedInheritanceDemo { // 実行クラス
    public static void main(String[] args) {
        PaymentService service = new CardPaymentService(); // 抽象型で受ける（多態性）
        Notifier notifier = new ConsoleNotifier(); // インターフェース型で受ける

        int fee = service.calculateFee(5000); // 手数料計算
        notifier.notifyResult("手数料: " + fee); // 通知処理
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 AdvancedInheritanceDemo.java
java AdvancedInheritanceDemo
```

期待出力例:
```text
[通知] 手数料: 50
```



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



