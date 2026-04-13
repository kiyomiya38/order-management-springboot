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

---

## 3. 先に覚えるポイント
1. Stream は「データに対する処理パイプライン」
2. `filter` で絞り込み、`map` で変換、`collect` で結果化
3. 集計は `count`, `mapToInt(...).sum()` が基本

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
import java.util.List;

public class StreamApiDemo {
    public static void main(String[] args) {
        List<String> statuses = List.of("PAID", "PENDING", "PAID", "CANCELLED");

        long paidCount = statuses.stream()
                .filter(s -> s.equals("PAID"))
                .count();

        System.out.println("PAID件数: " + paidCount);
    }
}
```

実行:
```bash
javac -encoding UTF-8 StreamApiDemo.java
java StreamApiDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: map と forEach
`StreamApiDemo.java` を次の内容に更新:

```java
import java.util.List;

public class StreamApiDemo {
    public static void main(String[] args) {
        List<String> orderCodes = List.of("ord-001", "ord-002", "ord-003");

        orderCodes.stream()
                .map(String::toUpperCase)
                .forEach(code -> System.out.println("注文コード: " + code));
    }
}
```

実行:
```bash
javac -encoding UTF-8 StreamApiDemo.java
java StreamApiDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: 数値集計を追加（仕上げ）
`StreamApiDemo.java` を次の内容に更新:

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
    }
}
```

実行:
```bash
javac -encoding UTF-8 StreamApiDemo.java
java StreamApiDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `amounts` から最大値を取得
2. `status` の `PAID` だけを `List` として取得
3. `map` で `"ORD-" + 番号` 形式に変換して表示

---

## 6. つまずきポイント
- `stream()` 呼び出し位置が違う
  -> `List` などのコレクションに対して呼ぶ
- ラムダ式の型不一致
  -> `map` と `mapToInt` の違いを確認
- 処理が読みづらい
  -> 1行1処理（filter/map/sum）で改行して書く
