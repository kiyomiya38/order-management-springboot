# Java-05 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-05-class-libraries.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3の`normalized`を宣言した直後へ、文字数を受け取る変数を追加する。`length()`の戻り値は`int`型になる。

```java
int rawLength = rawName.length();
int normalizedLength = normalized.length();
```

既存の文字列表示処理の後へ、次の2行を追加する。

```java
System.out.println("trim前 length: " + rawLength);
System.out.println("trim後 length: " + normalizedLength);
```

`price` と `taxRate` を次のように変更する。

```java
int price = 1980;
double taxRate = 0.08;
```

期待出力例:
```text
trim前 length: 13
trim後 length: 9
税込価格(四捨五入): 2138
```

---

## レベル2（拡張）解答
レベル1の完成コードを引き継ぎ、既存の `today` と `orderId` を利用して次の処理を追加する。その他の処理は残す。

```java
LocalDate threeDaysLater = today.plusDays(3);
String secondOrderId = UUID.randomUUID().toString();

System.out.println("3日後: " + threeDaysLater);
System.out.println("UUID-1: " + orderId);
System.out.println("UUID-2: " + secondOrderId);
```

確認ポイント:
- `plusDays(3)`は3日後の新しい`LocalDate`を返し、`today`自体は変更しない
- `threeDaysLater` は実行日の3日後になる
- `orderId` と `secondOrderId` は通常、異なる値になる

---

## レベル3（実務）解答
レベル2の完成コードを引き継ぎ、次の2行を追加する。その他の処理は残す。
```java
String businessOrderId = "ORD-" + orderId;
System.out.println(today + " / " + businessOrderId);
```

期待出力例:
```text
2026-05-29 / ORD-123e4567-e89b-12d3-a456-426614174000
```

日付とUUID部分は実行するたびに変わる。

### レベル3完了時の全コード

```java
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LibraryDemo {
    public static void main(String[] args) {
        String rawName = "  Shinesoft  ";
        String normalized = rawName.trim();
        int rawLength = rawName.length();
        int normalizedLength = normalized.length();

        int price = 1980;
        double taxRate = 0.08;
        int taxed = (int) Math.round(price * (1 + taxRate));
        int max = Math.max(900, taxed);

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate threeDaysLater = today.plusDays(3);
        LocalDateTime now = LocalDateTime.now();
        String orderId = UUID.randomUUID().toString();
        String secondOrderId = UUID.randomUUID().toString();
        String businessOrderId = "ORD-" + orderId;

        System.out.println("元の文字列: [" + rawName + "]");
        System.out.println("整形後: [" + normalized + "]");
        System.out.println("空白だけか: " + "   ".isBlank());
        System.out.println("trim前 length: " + rawLength);
        System.out.println("trim後 length: " + normalizedLength);
        System.out.println("税込価格(四捨五入): " + taxed);
        System.out.println("比較結果(大きい方): " + max);
        System.out.println("営業日: " + today);
        System.out.println("翌日: " + tomorrow);
        System.out.println("処理時刻: " + now);
        System.out.println("注文ID: " + orderId);
        System.out.println("3日後: " + threeDaysLater);
        System.out.println("UUID-1: " + orderId);
        System.out.println("UUID-2: " + secondOrderId);
        System.out.println(today + " / " + businessOrderId);
    }
}
```

---

## 実行前予想問題の解答
1. `"  ABC  ".trim().length()` -> `3`  
2. `"ABC".length()` -> `3`

---

## デバッグ演習（任意）の解答
`import java.time.LocalDate;` を外すと、`LocalDate` の場所を解決できず、`cannot find symbol` が表示される。

確認する内容:
- エラーに `symbol: class LocalDate` が含まれている
- `LocalDate` を使用している行がエラー位置として表示されている

`import java.time.LocalDate;` を戻して再コンパイルすれば解消する。
