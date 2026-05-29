# Java-19 ハンズオン: Stream API

対応参考資料: `Java-19_StreamAPI.pptx`

## 1. この資料のゴール
- Stream API の基本（`filter`, `map`, `forEach`）を使える
- 集計（`count`, `sum`）の簡単な処理を書ける
- ループ処理を宣言的に置き換えるメリットを理解できる

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
1. Stream は「データに対する処理パイプライン」
2. `filter` で絞り込み、`map` で変換、`collect` で結果化
3. 集計は `count`, `mapToInt(...).sum()` が基本

先取り補足:
- `s -> s.equals("PAID")` はラムダ式で、「1件分の値 `s` を受け取り、条件結果を返す処理」と読む
- `String::toUpperCase` や `Integer::intValue` はメソッド参照で、「その型のメソッドを各要素に適用する」と読む
- ラムダ式とメソッド参照は、ここでは Stream の処理部品として使いながら慣れればよい

---

## 4. ハンズオン

目的:
- 実務でよくある絞り込み・変換・集計をStreamで書く

完了条件:
- `StreamApiDemo.java` で絞り込み・集計・変換を実行できる

作成ファイル: `~/order-management-springboot/practice/java/handson19/StreamApiDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson19
cd ~/order-management-springboot/practice/java/handson19
```

### Step 1: filter と count
`StreamApiDemo.java` を次の内容で作成:

```java
import java.util.List; // List 利用のための import

public class StreamApiDemo { // Stream API 入門クラス
    public static void main(String[] args) {
        List<String> statuses = List.of("PAID", "PENDING", "PAID", "CANCELLED"); // 注文状態一覧

        long paidCount = statuses.stream() // List から Stream を生成
                .filter(s -> s.equals("PAID")) // PAID だけに絞り込む
                .count(); // 件数を数える

        System.out.println("PAID件数: " + paidCount); // 集計結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 StreamApiDemo.java
java StreamApiDemo
```

期待出力例:
```text
PAID件数: 2
```



### Step 2: map と forEach
`StreamApiDemo.java` を次の内容に更新:

```java
import java.util.List; // List 利用のための import

public class StreamApiDemo { // map と forEach の利用例
    public static void main(String[] args) {
        List<String> orderCodes = List.of("ord-001", "ord-002", "ord-003"); // 小文字コード一覧

        orderCodes.stream() // Stream を生成
                .map(String::toUpperCase) // 各要素を大文字へ変換
                .forEach(code -> System.out.println("注文コード: " + code)); // 変換後要素を1件ずつ表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 StreamApiDemo.java
java StreamApiDemo
```

期待出力例:
```text
注文コード: ORD-001
注文コード: ORD-002
注文コード: ORD-003
```



### Step 3: 数値集計を追加（仕上げ）
`StreamApiDemo.java` を次の内容に更新:

```java
import java.util.List; // List 利用のための import

public class StreamApiDemo { // 数値集計の利用例
    public static void main(String[] args) {
        List<Integer> amounts = List.of(1200, 3000, 800, 4500); // 金額一覧

        int total = amounts.stream() // Stream を生成
                .filter(a -> a >= 1000) // 1000円以上に絞り込む
                .mapToInt(Integer::intValue) // IntStream へ変換
                .sum(); // 合計値を計算

        System.out.println("1000円以上の合計: " + total); // 集計結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 StreamApiDemo.java
java StreamApiDemo
```

期待出力例:
```text
1000円以上の合計: 8700
```



---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `amounts` から最大値を取得する。

期待出力例:
```text
最大金額: 4500
```

### レベル2（拡張）
1. `statuses` の `PAID` だけを `List` として取得する。

期待出力例:
```text
[PAID, PAID]
```

### レベル3（実務）
1. `map` で `"ORD-" + 番号` 形式に変換して表示する。

期待出力例:
```text
ORD-1
ORD-2
ORD-3
```

---

## 6. つまずきポイント
- `stream()` 呼び出し位置が違う
  -> `List` などのコレクションに対して呼ぶ
- ラムダ式の型不一致
  -> `map` と `mapToInt` の違いを確認
- 処理が読みづらい
  -> 1行1処理（filter/map/sum）で改行して書く



