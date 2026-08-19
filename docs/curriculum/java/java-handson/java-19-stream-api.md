# Java-19 ハンズオン: Stream API

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

### `for`文との違い

Stream APIを使わなくても、同じ結果は`for`文で作れます。違いは、処理手順を自分で細かく書くか、データに対して「何をしたいか」をつなげて書くかです。

```java
// for文: 値を1件ずつ取り出す手順も自分で書く
long paidCount = 0;
for (String status : statuses) {
    if (status.equals("PAID")) {
        paidCount++;
    }
}
```

```java
// Stream: 絞り込んで数える、という目的をつなげて書く
long paidCount = statuses.stream()
        .filter(status -> status.equals("PAID"))
        .count();
```

| 部分 | 読み方 |
| --- | --- |
| `statuses.stream()` | `statuses`を順番に処理する流れを作る |
| `.filter(...)` | 条件に合う要素だけ残す |
| `.count()` | 残った件数を数えて結果を確定する |

`filter`や`map`だけでは処理結果は確定しません。`count`、`sum`、`toList`、`forEach`などの終端操作までつながったときに処理されます。

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
import java.util.List; // List を使うために必要な準備

public class StreamApiDemo { // このプログラム全体をまとめるクラス
    public static void main(String[] args) {
        List<String> statuses = List.of("PAID", "PENDING", "PAID", "CANCELLED"); // 注文ごとの状態を文字列の一覧として用意

        // 次の3行は、読みやすくするために改行しているだけで、; までが1つの文
        // 行頭の .filter や .count は、前の行の結果に続けて処理を呼び出している
        // このように . で処理をつなげる書き方をメソッドチェーンと呼ぶ
        // count() の結果は long 型で返るため、受け取る変数も long にする
        long paidCount = statuses.stream() // statuses の中身を1件ずつ順番に処理する流れを作る
                .filter(s -> s.equals("PAID")) // filter は「条件に合うものだけ残す」処理。s は1件分の注文状態
                .count(); // filter で残ったデータの件数を数える。count() の結果は long 型

        System.out.println("PAID件数: " + paidCount); // 数えた PAID の件数を画面に表示する
    } // main メソッドの処理はここまで
} // StreamApiDemo クラスの定義はここまで
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
import java.util.List; // List を使うために必要な準備

public class StreamApiDemo { // このプログラム全体をまとめるクラス
    public static void main(String[] args) {
        // ===== Step 2 で追加・変更 =====
        List<String> orderCodes = List.of("ord-001", "ord-002", "ord-003"); // 小文字の注文コードを文字列の一覧として用意

        orderCodes.stream() // orderCodes の中身を1件ずつ順番に処理する流れを作る
                .map(String::toUpperCase) // map は「別の値に変換する」処理。各注文コードを大文字に変換する
                .forEach(code -> System.out.println("注文コード: " + code)); // forEach は残った値を1件ずつ取り出して処理する。code は1件分の注文コード
        // ===== Step 2 で追加・変更ここまで =====
    } // main メソッドの処理はここまで
} // StreamApiDemo クラスの定義はここまで
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
import java.util.List; // List を使うために必要な準備

public class StreamApiDemo { // このプログラム全体をまとめるクラス
    public static void main(String[] args) {
        // ===== Step 3 で追加・変更 =====
        List<Integer> amounts = List.of(1200, 3000, 800, 4500); // 注文金額を数値の一覧として用意

        // sum() の結果は int 型で返るため、受け取る変数も int にする
        int total = amounts.stream() // amounts の中身を1件ずつ順番に処理する流れを作る
                .filter(a -> a >= 1000) // filter は「条件に合うものだけ残す」処理。a は1件分の注文金額
                .mapToInt(Integer::intValue) // mapToInt は合計できる数値用の流れに変換する処理
                .sum(); // mapToInt で変換した数値をすべて足し合わせる。sum() の結果は int 型

        System.out.println("1000円以上の合計: " + total); // 計算した合計金額を画面に表示する
        // ===== Step 3 で追加・変更ここまで =====
    } // main メソッドの処理はここまで
} // StreamApiDemo クラスの定義はここまで
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

### 最大値を取得する`max()`と`orElse(...)`

ミニ演習では、合計に加えて最大値も取得します。最大値の取得は次のように書きます。

```java
List<Integer> amounts = List.of(1200, 3000, 800, 4500);

int maxAmount = amounts.stream()
        .mapToInt(Integer::intValue) // intとして集計できる流れへ変換する
        .max()                      // 最大値を探す
        .orElse(0);                 // データが空で最大値がなければ0を返す

System.out.println("最大金額: " + maxAmount);
```

`max()`は最大値を探しますが、空のリストには最大値がありません。そのため、`max()`の結果は「値が存在する場合と、存在しない場合がある」という形で返ります。`orElse(0)`は、最大値が存在しない場合に代わりに`0`を返します。

```text
データがある
  → max()で最大値を取得

データがない
  → 最大値が存在しない
  → orElse(0)の0を使用
```

ここで使用する`0`は例です。実務では、データが空の場合に`0`とするか、エラーとするかを業務ルールに合わせて決めます。



---

## 5. ミニ演習（10分）
Step 3の完成コードを基準に、レベル1からレベル3まで順番に進めてください。既存の`amounts`と合計処理を残したまま、各レベルの処理を`main(...)`へ追記します。

### レベル1（基本）
1. 直前の「最大値を取得する`max()`と`orElse(...)`」を参考に、Step 3にある既存の`amounts`から、`stream()`、`mapToInt(...)`、`max()`、`orElse(0)`を使って最大値を取得する。
2. 最大値を`int max`へ代入する。
3. `System.out.println("最大金額: " + max);`で表示する。

確認対象の出力（抜粋）:
```text
最大金額: 4500
```

### レベル2（拡張）
1. レベル1の表示より後へ、`List<String> statuses = List.of("PAID", "PENDING", "PAID", "CANCELLED");`を追加する。
2. `statuses.stream()`、`filter(...)`、`toList()`を使って`"PAID"`だけを抽出する。
3. 抽出結果を`List<String> paidOnly`へ代入し、`System.out.println(paidOnly);`で表示する。

確認対象の出力（抜粋）:
```text
[PAID, PAID]
```

### レベル3（実務）
1. レベル2の表示より後へ、`List<Integer> numbers = List.of(1, 2, 3);`を追加する。
2. `numbers.stream()`へ`map(n -> "ORD-" + n)`を適用する。
3. `forEach(System.out::println)`で変換後の注文番号を1行ずつ表示する。

確認対象の出力（抜粋）:
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
