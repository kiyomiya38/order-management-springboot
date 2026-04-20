# Java-07 ハンズオン: 配列

対応参考資料: `Java-07_配列.pptx`
補講（任意）: [Java-07A 参照型と多次元配列](./java-07a-reference-types-and-multidimensional-arrays.md)

## 1. この資料のゴール
- 配列の宣言・初期化・参照を理解する
- `for` と組み合わせて配列を処理できる
- インデックスと `length` の関係を説明できる

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
1. 配列は同じ型の値を複数保持する
2. 先頭要素のインデックスは `0`
3. 有効範囲は `0` 〜 `length - 1`

---

## 4. ハンズオン

目的:
- 複数データをまとめて扱う

完了条件:
- `ArrayDemo.java` で数値配列と文字列配列を処理できる

作成ファイル: `~/order-management-springboot/practice/java/handson07/ArrayDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson07
cd ~/order-management-springboot/practice/java/handson07
```

### Step 1: 数値配列を作る
`ArrayDemo.java` を次の内容で作成:

```java
public class ArrayDemo { // 配列の基本確認クラス
    public static void main(String[] args) { // 実行開始地点
        int[] quantities = {3, 5, 2, 8}; // int 配列を初期化（4要素）
        System.out.println("1件目数量: " + quantities[0]); // インデックス 0 の要素を参照
        System.out.println("配列の長さ: " + quantities.length); // 要素数を取得
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ArrayDemo.java
java ArrayDemo
```

期待出力例:
```text
1件目数量: 3
配列の長さ: 4
```



### Step 2: ループで合計する
`ArrayDemo.java` を次の内容に更新:

```java
public class ArrayDemo { // 配列をループで集計するクラス
    public static void main(String[] args) {
        int[] quantities = {3, 5, 2, 8}; // 集計対象の配列
        int total = 0; // 合計値の初期値

        for (int i = 0; i < quantities.length; i++) { // i を配列の有効範囲で繰り返す
            total += quantities[i]; // 現在要素を total に加算
        }

        System.out.println("数量合計: " + total); // 集計結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ArrayDemo.java
java ArrayDemo
```

期待出力例:
```text
数量合計: 18
```



### Step 3: 文字列配列を追加（仕上げ）
`ArrayDemo.java` を次の内容に更新:

```java
public class ArrayDemo { // 文字列配列と数値配列を組み合わせて扱うクラス
    public static void main(String[] args) {
        String[] productNames = {"Laptop", "Mouse", "Keyboard"}; // 商品名配列
        int[] quantities = {3, 5, 2}; // 各商品の数量配列

        for (int i = 0; i < productNames.length; i++) { // 商品数分だけ繰り返す
            System.out.println(productNames[i] + " 数量: " + quantities[i]); // 同じインデックス同士を対応付けて表示
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ArrayDemo.java
java ArrayDemo
```

期待出力:
```text
Laptop 数量: 3
Mouse 数量: 5
Keyboard 数量: 2
```

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. 商品を1件追加して4件表示する。

期待出力例:
```text
Laptop 数量: 3
Mouse 数量: 5
Keyboard 数量: 2
Monitor 数量: 1
```

### レベル2（拡張）
1. `quantities` の最大値を求める処理を追加する。

期待出力例:
```text
最大値: 8
```

### レベル3（実務）
1. `for` を拡張for（for-each）に書き換えできるか試す。
2. インデックスが必要な場面では通常 `for` を残し、用途で使い分ける。

期待出力例:
```text
合計(拡張for): 18
0: Laptop
1: Mouse
2: Keyboard
```

### 実行前予想問題（1分）
次のコードの出力を実行前に予想してください。
- `int[] nums = {3, 5, 2, 8};`
- `System.out.println(nums.length);`
- `System.out.println(nums[nums.length - 1]);`

### デバッグ演習（任意, 5分）
1. ループ条件を `i < quantities.length` から `i <= quantities.length` に変更して実行する。
2. `ArrayIndexOutOfBoundsException` を確認する。
3. 条件を元に戻して再実行する。

---

## 6. つまずきポイント
- `ArrayIndexOutOfBoundsException`
  -> `i < array.length` を確認
- 配列長が一致しない
  -> `productNames` と `quantities` の件数を揃える
- `length()` と `length` の混同
  -> 配列は `length`（フィールド）



