# Java-11A 補講: コンストラクタ連鎖（`this()` / デフォルトコンストラクタ）

対応参考資料: `J2_02_コンストラクタ.pdf`

## 1. この資料のゴール
- コンストラクタのオーバーロードを実装できる
- `this()` で初期化処理を集約できる
- デフォルトコンストラクタが補われる条件を説明できる

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
1. コンストラクタはメソッドと同様にオーバーロードできる
2. `this(...)` は同一クラス内の別コンストラクタ呼び出し
3. `this(...)` はコンストラクタの先頭に1回だけ書ける
4. コンストラクタを1つも定義しないときだけ、引数なしコンストラクタが自動補完される

---

## 4. ハンズオン

目的:
- 重複初期化を避けたクラス設計を体験する

完了条件:
- `ConstructorChainingDemo.java` で `this()` 連鎖と自動補完ルールを確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson11a/ConstructorChainingDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson11a
cd ~/order-management-springboot/practice/java/handson11a
```

### Step 1: コンストラクタをオーバーロードする
`ConstructorChainingDemo.java` を次の内容で作成:

```java
class Product {
    String name;
    int price;

    Product() { // 引数なし
        this.name = "UNKNOWN";
        this.price = 0;
    }

    Product(String name) { // 引数1つ
        this.name = name;
        this.price = 0;
    }

    Product(String name, int price) { // 引数2つ
        this.name = name;
        this.price = price;
    }
}

public class ConstructorChainingDemo {
    public static void main(String[] args) {
        Product p1 = new Product();
        Product p2 = new Product("Mouse");
        Product p3 = new Product("Keyboard", 5000);

        System.out.println(p1.name + " / " + p1.price);
        System.out.println(p2.name + " / " + p2.price);
        System.out.println(p3.name + " / " + p3.price);
    }
}
```

実行:
```bash
javac -encoding UTF-8 ConstructorChainingDemo.java
java ConstructorChainingDemo
```

期待結果:
- 3パターンの初期化結果が表示される

### Step 2: `this()` で初期化処理を集約する
`ConstructorChainingDemo.java` を次の内容に更新:

```java
class Product {
    String name;
    int price;

    Product() {
        this("UNKNOWN", 0); // もっとも詳細なコンストラクタへ委譲
    }

    Product(String name) {
        this(name, 0); // 重複代入を避ける
    }

    Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

public class ConstructorChainingDemo {
    public static void main(String[] args) {
        Product p = new Product("Display");
        System.out.println(p.name + " / " + p.price);
    }
}
```

実行:
```bash
javac -encoding UTF-8 ConstructorChainingDemo.java
java ConstructorChainingDemo
```

期待結果:
- `Display / 0` が表示される

### Step 3: デフォルトコンストラクタの補完ルールを確認する（仕上げ）
`ConstructorChainingDemo.java` を次の内容に更新:

```java
class User {
    String name;

    User(String name) { // 引数ありコンストラクタを1つ定義
        this.name = name;
    }
}

public class ConstructorChainingDemo {
    public static void main(String[] args) {
        User ok = new User("Tanaka");
        System.out.println(ok.name);

        // User ng = new User(); // これを有効化するとコンパイルエラー:
        // constructor User in class User cannot be applied to given types
    }
}
```

実行:
```bash
javac -encoding UTF-8 ConstructorChainingDemo.java
java ConstructorChainingDemo
```

期待結果:
- `Tanaka` が表示される
- `new User()` は引数なしコンストラクタ未定義のため失敗する（コメント解除時）

---

## 5. ミニ演習（10分）
1. `Product(String name, int price, int quantity)` を追加し、他のコンストラクタから `this(...)` で委譲する。
2. Step 3 の `User` に引数なしコンストラクタを追加して `new User()` を成功させる。
3. `this(...)` の前に代入文を書いてコンパイルエラーを確認する。

---

## 6. つまずきポイント
- `this(...)` を2行目以降に書いてエラー
  -> 必ずコンストラクタ先頭に書く
- コンストラクタを作ったのに `new ClassName()` が失敗
  -> 引数なしは自動補完されない場合がある
- オーバーロードで初期化処理が重複
  -> `this(...)` で一本化する
