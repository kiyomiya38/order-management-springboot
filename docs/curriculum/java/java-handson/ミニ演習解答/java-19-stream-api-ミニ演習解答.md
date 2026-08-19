# Java-19 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-19-stream-api.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3の`amounts`はすでに定義されているため、作り直さずに既存の合計表示より後へ次の処理を追記する:

```java
// レベル1で追記
int max = amounts.stream()
        .mapToInt(Integer::intValue) // intとして集計できる流れへ変換
        .max()                      // 最大値を探す
        .orElse(0);                 // データが空なら0を使用
System.out.println("最大金額: " + max);
```

## レベル2（拡張）解答
レベル1の最大値取得処理を残したまま、次の処理を追記する:

```java
// レベル2で追記
List<String> statuses = List.of("PAID", "PENDING", "PAID", "CANCELLED");
List<String> paidOnly = statuses.stream()
        .filter(status -> status.equals("PAID")) // PAIDだけを残す
        .toList();
System.out.println(paidOnly); // [PAID, PAID]
```

## レベル3（実務）解答
レベル1・2の処理を残したまま、次の処理を追記する:

```java
// レベル3で追記
List<Integer> numbers = List.of(1, 2, 3);
numbers.stream()
       .map(n -> "ORD-" + n)           // 数値をORD-付き文字列へ変換
       .forEach(System.out::println);   // 変換後の値を1件ずつ表示
```

### レベル3完了時の全コード

```java
import java.util.List;

public class StreamApiDemo {
    public static void main(String[] args) {
        List<Integer> amounts = List.of(1200, 3000, 800, 4500);

        int total = amounts.stream()
                .filter(a -> a >= 1000)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("1000円以上の合計: " + total);

        // ===== レベル1で追加: 最大金額を求める =====
        int max = amounts.stream()
                .mapToInt(Integer::intValue)
                .max() // 最大値を探す
                .orElse(0); // データが空なら0を使用
        System.out.println("最大金額: " + max);
        // ===== レベル1で追加ここまで =====

        // ===== レベル2で追加: PAIDだけを新しいListへまとめる =====
        List<String> statuses = List.of("PAID", "PENDING", "PAID", "CANCELLED");
        List<String> paidOnly = statuses.stream()
                .filter(status -> status.equals("PAID")) // PAIDだけを残す
                .toList();
        System.out.println(paidOnly);
        // ===== レベル2で追加ここまで =====

        // ===== レベル3で追加: 数値を注文番号へ変換して表示する =====
        List<Integer> numbers = List.of(1, 2, 3);
        numbers.stream()
                .map(n -> "ORD-" + n)
                .forEach(System.out::println);
        // ===== レベル3で追加ここまで =====
    }
}
```
