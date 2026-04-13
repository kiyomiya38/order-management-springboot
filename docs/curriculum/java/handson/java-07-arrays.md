# Java-07 ハンズオン: 配列

対応参考資料: `Java-07_配列.pptx`

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
public class ArrayDemo {
    public static void main(String[] args) {
        int[] quantities = {3, 5, 2, 8};
        System.out.println("1件目数量: " + quantities[0]);
        System.out.println("配列の長さ: " + quantities.length);
    }
}
```

実行:
```bash
javac -encoding UTF-8 ArrayDemo.java
java ArrayDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: ループで合計する
`ArrayDemo.java` を次の内容に更新:

```java
public class ArrayDemo {
    public static void main(String[] args) {
        int[] quantities = {3, 5, 2, 8};
        int total = 0;

        for (int i = 0; i < quantities.length; i++) {
            total += quantities[i];
        }

        System.out.println("数量合計: " + total);
    }
}
```

実行:
```bash
javac -encoding UTF-8 ArrayDemo.java
java ArrayDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: 文字列配列を追加（仕上げ）
`ArrayDemo.java` を次の内容に更新:

```java
public class ArrayDemo {
    public static void main(String[] args) {
        String[] productNames = {"Laptop", "Mouse", "Keyboard"};
        int[] quantities = {3, 5, 2};

        for (int i = 0; i < productNames.length; i++) {
            System.out.println(productNames[i] + " 数量: " + quantities[i]);
        }
    }
}
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
1. 商品を1件追加して4件表示する
2. `quantities` の最大値を求める処理を追加
3. `for` を拡張for（for-each）に書き換えできるか試す

---

## 6. つまずきポイント
- `ArrayIndexOutOfBoundsException`
  -> `i < array.length` を確認
- 配列長が一致しない
  -> `productNames` と `quantities` の件数を揃える
- `length()` と `length` の混同
  -> 配列は `length`（フィールド）
