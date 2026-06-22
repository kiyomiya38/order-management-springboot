# Java-11 ハンズオン: さまざまなクラス機構（constructor / this / static）

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

### 全体構成図（constructor / this / static）
```mermaid
flowchart TD
  NEW["new Product"] --> CTOR["コンストラクタ"]
  CTOR --> OBJ["Product インスタンス"]
  OBJ --> FIELD["name や price はインスタンスごと"]
  CTOR --> COUNT["生成数を増やす"]
  COUNT --> STATIC["createdCount はクラスで共有"]

  THIS["this"] --> FIELD
  UTIL["PriceUtil クラス"] --> SMETHOD["static メソッド"]
  SMETHOD -->|インスタンスなしで呼べる| MAIN["main から利用"]
```

ポイント:
- `new` するとコンストラクタが呼ばれ、インスタンスの初期値を設定する
- `this` は「いま作っている、または操作しているインスタンス」を指す
- `static` は個別インスタンスではなく、クラス側に1つある共有領域として考える

### 書式の基本

#### コンストラクタ

```java
class Product {
    String name;
    int price;

    Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

Product p = new Product("Laptop", 120000);
```

ポイント:
- コンストラクタ名はクラス名と同じ
- コンストラクタには戻り値の型を書かない
- `new Product(...)` のときにコンストラクタが呼ばれる
- `this.name` はフィールド、右辺の `name` は引数

#### `static` フィールド

```java
class Product {
    static int createdCount = 0;
}

System.out.println(Product.createdCount);
```

ポイント:
- `static` フィールドはクラス全体で共有される
- インスタンスごとではなく、クラスに1つだけ存在する
- クラス名経由で `Product.createdCount` のように参照できる

#### `static` メソッド

```java
class PriceUtil {
    static int calcTaxIncluded(int basePrice) {
        return basePrice * 110 / 100;
    }
}

int taxed = PriceUtil.calcTaxIncluded(5000);
```

ポイント:
- `static` メソッドはインスタンスを作らずに呼び出せる
- `クラス名.メソッド名(...)` の形で使う
- 共通計算や変換処理のように、個別の状態を持たない処理に向いている

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
        new Product("Laptop", 120000); // 1件目生成。変数に入れていないが、new によりコンストラクタが呼ばれ createdCount が増える
        new Product("Mouse", 2500); // 2件目生成。この例では生成数だけ確認したいため、Product p のような変数には代入しない

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
### レベル1（基本）
1. `Product` に `quantity` を追加し、コンストラクタで初期化する。

期待出力例:
```text
Keyboard quantity: 2
```

### レベル2（拡張）
1. `PriceUtil` に割引計算メソッドを追加する。

期待出力例:
```text
割引後価格: 4500
```

### レベル3（実務）
1. `createdCount` を表示するサンプルを再追加し、生成件数を確認する。

期待出力例:
```text
作成件数: 2
```

---

## 6. つまずきポイント
- コンストラクタ名がクラス名と一致していない
  -> 返り値なし・クラス名一致を確認
- `non-static variable ... cannot be referenced from a static context`
  -> `static` とインスタンス変数の区別を確認
- `this` を `static` メソッドで使ってしまう
  -> `this` はインスタンスメソッド/コンストラクタのみ



