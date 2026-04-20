# Java-11 ハンズオン: さまざまなクラス機構（constructor / this / static）

対応参考資料: `Java-11_さまざまなクラス機構.pptx`

## 1. この資料のゴール
- コンストラクタの役割を説明できる
- `this` と `static` の違いを理解できる
- クラス変数とインスタンス変数を使い分けできる

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
1. コンストラクタは `new` 時に呼ばれる初期化処理
2. `this` はインスタンス自身、`static` はクラス全体共有
3. `static` メソッドはインスタンスなしで呼べる

---

## 4. ハンズオン

目的:
- クラス機構の基本を実装で理解する

完了条件:
- `ClassMechanismDemo.java` で constructor / this / static を確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson11/ClassMechanismDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson11
cd ~/order-management-springboot/practice/java/handson11
```

### Step 1: コンストラクタで初期化する
`ClassMechanismDemo.java` を次の内容で作成:

```java
class Product { // 商品クラス
    String name; // 商品名
    int price; // 価格

    Product(String name, int price) { // コンストラクタ: new のときに呼ばれる初期化処理
        this.name = name; // 引数 name をフィールドへ設定
        this.price = price; // 引数 price をフィールドへ設定
    }
}

public class ClassMechanismDemo { // 実行クラス
    public static void main(String[] args) {
        Product p = new Product("Laptop", 120000); // コンストラクタで初期化しながら生成
        System.out.println(p.name + " / " + p.price); // 設定された値を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ClassMechanismDemo.java
java ClassMechanismDemo
```

期待出力例:
```text
Laptop / 120000
```



### Step 2: static フィールドを追加する
`ClassMechanismDemo.java` を次の内容に更新:

```java
class Product { // 商品クラス
    static int createdCount = 0; // static: 全インスタンスで共有する生成数カウンタ
    String name; // 商品名
    int price; // 価格

    Product(String name, int price) { // コンストラクタ
        this.name = name; // フィールド初期化
        this.price = price; // フィールド初期化
        createdCount++; // インスタンス生成ごとに共有カウンタを増やす
    }
}

public class ClassMechanismDemo { // 実行クラス
    public static void main(String[] args) {
        new Product("Laptop", 120000); // 1件目生成
        new Product("Mouse", 2500); // 2件目生成

        System.out.println("生成数: " + Product.createdCount); // クラス名経由で static フィールドを参照
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ClassMechanismDemo.java
java ClassMechanismDemo
```

期待出力例:
```text
生成数: 2
```



### Step 3: static メソッドを追加（仕上げ）
`ClassMechanismDemo.java` を次の内容に更新:

```java
class PriceUtil { // 価格計算ユーティリティクラス
    static int calcTaxIncluded(int basePrice) { // static: インスタンス化せず呼べる計算メソッド
        return basePrice * 110 / 100; // 税込(10%)を整数計算で求める
    }
}

class Product { // 商品クラス
    String name; // 商品名
    int price; // 税抜価格

    Product(String name, int price) { // コンストラクタ
        this.name = name; // 名前初期化
        this.price = price; // 価格初期化
    }
}

public class ClassMechanismDemo { // 実行クラス
    public static void main(String[] args) {
        Product p = new Product("Keyboard", 5000); // 商品生成
        int taxed = PriceUtil.calcTaxIncluded(p.price); // static メソッドで税込計算
        System.out.println(p.name + " 税込: " + taxed); // 結果表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ClassMechanismDemo.java
java ClassMechanismDemo
```

期待出力例:
```text
Keyboard 税込: 5500
```



---

## 5. ミニ演習（10分）
1. `Product` に `quantity` を追加し、コンストラクタで初期化
2. `PriceUtil` に割引計算メソッドを追加
3. `createdCount` を表示するサンプルを再追加

---

## 6. つまずきポイント
- コンストラクタ名がクラス名と一致していない
  -> 返り値なし・クラス名一致を確認
- `non-static variable ... cannot be referenced from a static context`
  -> `static` とインスタンス変数の区別を確認
- `this` を `static` メソッドで使ってしまう
  -> `this` はインスタンスメソッド/コンストラクタのみ



