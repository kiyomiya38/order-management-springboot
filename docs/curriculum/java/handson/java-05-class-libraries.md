# Java-05 ハンズオン: 代表的なクラスライブラリ

対応参考資料: `Java-05_代表的なクラスライブラリ.pptx`

## 1. この資料のゴール
- Java標準ライブラリの基本的な使い方を理解する
- `String`, `Math`, `LocalDate`, `UUID` を実務用途で使える
- `import` の意味を説明できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. 標準ライブラリは JDK に含まれている
2. `import` は別パッケージのクラスを短く書くための宣言
3. 便利メソッドを使うと、自作コードを減らせる

---

## 4. ハンズオン

目的:
- 実務で頻出の標準クラスを使う

完了条件:
- 文字列整形、数学計算、日付取得、ID生成を1つのプログラムで実行できる

作成ファイル: `~/order-management-springboot/practice/java/handson05/LibraryDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson05
cd ~/order-management-springboot/practice/java/handson05
```

### Step 1: Stringメソッドを使う
`LibraryDemo.java` を次の内容で作成:

```java
public class LibraryDemo {
    public static void main(String[] args) {
        String rawName = "  Shinesoft  ";
        String normalized = rawName.trim();

        System.out.println("元の文字列: [" + rawName + "]");
        System.out.println("整形後: [" + normalized + "]");
        System.out.println("空白だけか: " + "   ".isBlank());
    }
}
```

実行:
```bash
javac -encoding UTF-8 LibraryDemo.java
java LibraryDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: Mathクラスを追加
`LibraryDemo.java` を次の内容に更新:

```java
public class LibraryDemo {
    public static void main(String[] args) {
        int price = 1280;
        double taxRate = 0.10;
        int taxed = (int) Math.round(price * (1 + taxRate));
        int max = Math.max(900, taxed);

        System.out.println("税込価格(四捨五入): " + taxed);
        System.out.println("比較結果(大きい方): " + max);
    }
}
```

実行:
```bash
javac -encoding UTF-8 LibraryDemo.java
java LibraryDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


コード解説:
- `Math.round` は四捨五入
- `Math.max` は大きい方を返す

### Step 3: 日付とIDを追加（仕上げ）
`LibraryDemo.java` を次の内容に更新:

```java
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LibraryDemo {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        String orderId = UUID.randomUUID().toString();

        System.out.println("営業日: " + today);
        System.out.println("処理時刻: " + now);
        System.out.println("注文ID: " + orderId);
    }
}
```

実行:
```bash
javac -encoding UTF-8 LibraryDemo.java
java LibraryDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


学習ポイント:
- `java.time` は日付時刻の標準API
- `UUID` は重複しにくい識別子生成に使う

---

## 5. ミニ演習（10分）
1. `LocalDate.now()` を `plusDays(3)` して3日後を表示
2. `UUID` を2回生成して値が異なることを確認
3. `trim()` 前後の文字列長を `length()` で比較

---

## 6. つまずきポイント
- `cannot find symbol`（`LocalDate` など）
  -> `import` 文を確認
- `NullPointerException`
  -> `null` の文字列にメソッドを呼んでいないか確認
- 日付/時刻の型を混同
  -> 日付のみは `LocalDate`、日時は `LocalDateTime`
