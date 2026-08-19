# Java-16 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-16-standard-classes.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 8の`Product`へ次のフィールドを追記し、既存のコンストラクタと`toString()`を変更する:

```java
// レベル1で追記
String name;
int price;

// レベル1で変更
Product(String code, String name, int price) {
    this.code = code;
    this.name = name;
    this.price = price;
}

@Override
public String toString() {
    return "Product{code='" + code + "', name='" + name + "', price=" + price + "}";
}
```

`main`の生成処理も、演習で指定した商品名と価格へ変更する。`equals(...)`は商品コードで比較するため、商品名と価格も同じ値にする:

```java
Product p1 = new Product("P-001", "Keyboard", 3000);
Product p2 = new Product("P-001", "Keyboard", 3000);
```

`quantityText`を一時的に`"12A"`へ変更して実行すると、`NumberFormatException`で処理が止まる。確認後は`"12"`へ戻してからレベル2へ進む。この時点では例外処理をまだ学んでいないため、`try` / `catch`の追加は不要。

## レベル2（拡張）解答
レベル1の`name`と`price`を残したまま、`equals(...)`と同じ`code`を基準に`hashCode()`を追記する:

```java
// レベル2で追記
@Override
public int hashCode() {
    return Objects.hash(code);
}
```

`Objects.hash(code)`は、`code`をもとにハッシュ値を作る。`equals(...)`と同じ`code`を基準にすることで、同じ商品と判定される`p1`と`p2`は同じハッシュ値になる。

`main`の`p1`と`p2`を生成した後へ、ハッシュ値の確認を追記する。`HashSet`はJava-18で学習するため、ここでは使用しない:

```java
// レベル2で追記
System.out.println("hashCode一致: " + (p1.hashCode() == p2.hashCode())); // true
```

既存の`StringBuilder`処理は次の3行へ変更する:

```java
StringBuilder log = new StringBuilder();
log.append("START").append(System.lineSeparator()); // STARTの後で改行
log.append("PROCESS").append(System.lineSeparator()); // PROCESSの後で改行
log.append("END"); // 最後の行なので改行は追加しない
System.out.println(log);
```

## レベル3（実務）解答
レベル1・2の変更を残したまま、既存の`body`だけを変更する:

```java
String body = "{\"name\":\"Suzuki\"}"; // レベル3で変更
```

`final` の挙動確認:
確認手順:
1. `private static final Path STATIC_DIR = Path.of("static");` の `final` を外す
2. `main`の先頭で`STATIC_DIR = Path.of("static2");`を代入し、コンパイルと実行ができることを確認する
3. 確認後は再代入行を削除し、`final`を戻す。レベル1・2の変更は戻さない

### レベル3完了時の全コード

```java
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Product {
    String code;
    // ===== レベル1で追加 =====
    String name;
    int price;
    // ===== レベル1で追加ここまで =====

    // ===== レベル1で変更: nameとpriceも初期化する =====
    Product(String code, String name, int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }
    // ===== レベル1で変更ここまで =====

    // ===== レベル1で変更: 3つの値を表示する =====
    @Override
    public String toString() {
        return "Product{code='" + code + "', name='" + name + "', price=" + price + "}";
    }
    // ===== レベル1で変更ここまで =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Product other)) {
            return false;
        }
        return Objects.equals(code, other.code);
    }

    // ===== レベル2で追加: equalsと同じcodeからハッシュ値を作る =====
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    // ===== レベル2で追加ここまで =====
}

public class StandardClassDemo {
    private static final Path STATIC_DIR = Path.of("static");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"");

    public static void main(String[] args) {
        // ===== レベル1で変更 =====
        Product p1 = new Product("P-001", "Keyboard", 3000);
        Product p2 = new Product("P-001", "Keyboard", 3000);
        // ===== レベル1で変更ここまで =====
        System.out.println(p1);
        System.out.println("同じ商品: " + p1.equals(p2));
        // ===== レベル2で追加 =====
        System.out.println("hashCode一致: " + (p1.hashCode() == p2.hashCode()));
        // ===== レベル2で追加ここまで =====

        String quantityText = "12";
        int quantity = Integer.parseInt(quantityText);
        Integer boxed = quantity;
        int unboxed = boxed;
        System.out.println("数量: " + unboxed);

        // ===== レベル2で変更: 3行のログを組み立てる =====
        StringBuilder log = new StringBuilder();
        log.append("START").append(System.lineSeparator());
        log.append("PROCESS").append(System.lineSeparator());
        log.append("END");
        System.out.println(log);
        // ===== レベル2で変更ここまで =====

        // ===== レベル3で変更: 抽出対象のnameをSuzukiへ変更 =====
        String body = "{\"name\":\"Suzuki\"}";
        // ===== レベル3で変更ここまで =====
        Matcher matcher = NAME_PATTERN.matcher(body);
        if (matcher.find()) {
            System.out.println("抽出名: " + matcher.group(1));
        }
        System.out.println("static dir: " + STATIC_DIR);
    }
}
```
