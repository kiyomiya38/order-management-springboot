# Java-10 ハンズオン: インスタンスとクラス

対応参考資料: `Java-10_インスタンスとクラス.pptx`
前章とのつながり: [Java-09 複数クラスを用いた開発](./java-09-multi-class-development.md) では `new` を先に使って連携を体験した。この章で「クラスは設計図 / インスタンスは実体」を整理する。

## 1. この資料のゴール
- クラスとインスタンスの違いを説明できる
- 同じクラスから複数インスタンスを作成し、状態が独立することを確認できる
- `this` の基本的な意味を理解する

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
1. この章の流れは「フィールド直接代入 -> メソッド経由の更新 -> `this` による明示」の3段階で、同じデータを別の書き方で扱えるようにすることが目的
2. クラスは設計図、インスタンスは実体。`new` で作った各インスタンスは別状態を持つ（`c1` を変えても `c2` は自動では変わらない）
3. `this` は「今このインスタンス自身」。引数名とフィールド名が同じとき（例: `name`）に、`this.name` と書いて「フィールド側」を明確にするために使う

---

## 4. ハンズオン

目的:
- インスタンスが独立した状態を持つことを理解する

完了条件:
- `InstanceDemo.java` で2つのオブジェクトを生成し、値が独立していることを確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson10/InstanceDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson10
cd ~/order-management-springboot/practice/java/handson10
```

### Step 1: クラスとインスタンスを作る
`InstanceDemo.java` を次の内容で作成:

```java
class Customer { // 顧客データを表すクラス
    String name; // 顧客名
    int point; // 保有ポイント
}

public class InstanceDemo { // 実行クラス
    public static void main(String[] args) {
        Customer c1 = new Customer(); // 1人目のインスタンス生成
        c1.name = "Tanaka"; // 1人目の名前
        c1.point = 120; // 1人目のポイント

        Customer c2 = new Customer(); // 2人目のインスタンス生成（c1とは別実体）
        c2.name = "Suzuki"; // 2人目の名前
        c2.point = 80; // 2人目のポイント

        System.out.println(c1.name + " point: " + c1.point); // c1 の状態を表示
        System.out.println(c2.name + " point: " + c2.point); // c2 の状態を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 InstanceDemo.java
java InstanceDemo
```

期待出力例:
```text
Tanaka point: 120
Suzuki point: 80
```


期待出力例:
```text
Tanaka point: 120
Suzuki point: 80
```


### Step 2: メソッドを追加する
`InstanceDemo.java` を次の内容に更新:

```java
class Customer { // 顧客クラス
    String name; // 顧客名
    int point; // ポイント

    void addPoint(int value) { // ポイント加算メソッド
        point += value; // 現在ポイントに value を加える
    }
}

public class InstanceDemo { // 実行クラス
    public static void main(String[] args) {
        Customer c1 = new Customer(); // 1人目を生成
        c1.name = "Tanaka"; // 名前設定
        c1.point = 120; // 初期ポイント設定
        c1.addPoint(30); // メソッド呼び出しで加算

        Customer c2 = new Customer(); // 2人目を生成
        c2.name = "Suzuki"; // 名前設定
        c2.point = 80; // 初期ポイント設定

        System.out.println(c1.name + " point: " + c1.point); // 加算後の c1 を表示
        System.out.println(c2.name + " point: " + c2.point); // c2 は影響を受けないことを表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 InstanceDemo.java
java InstanceDemo
```

期待出力例:
```text
Tanaka point: 150
Suzuki point: 80
```


期待出力例:
```text
Tanaka point: 150
Suzuki point: 80
```

確認ポイント:
- `addPoint(30)` を呼んだ `c1` だけが変化し、`c2` は変化しない


### Step 3: `this` を使う（仕上げ）
`InstanceDemo.java` を次の内容に更新:

```java
class Customer { // 顧客クラス
    String name; // 顧客名
    int point; // ポイント

    void setProfile(String name, int point) { // 顧客情報を一括設定するメソッド
        this.name = name; // this.name はフィールド、name は引数
        this.point = point; // this.point はフィールド、point は引数
    }
}

public class InstanceDemo { // 実行クラス
    public static void main(String[] args) {
        Customer c1 = new Customer(); // 1人目を生成
        c1.setProfile("Tanaka", 120); // プロフィール設定

        Customer c2 = new Customer(); // 2人目を生成
        c2.setProfile("Suzuki", 80); // プロフィール設定

        System.out.println(c1.name + " point: " + c1.point); // c1 の状態を表示
        System.out.println(c2.name + " point: " + c2.point); // c2 の状態を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 InstanceDemo.java
java InstanceDemo
```

期待出力例:
```text
Tanaka point: 120
Suzuki point: 80
```


期待出力例:
```text
Tanaka point: 120
Suzuki point: 80
```

確認ポイント:
- Step 3 の主目的は出力の変化ではなく、`this` を使った代入方法の理解
- `this.name = name;` のように「フィールド」と「引数」を明確に区別できる


---

## 5. ミニ演習（10分）
1. `Customer` を3件作成して表示
2. `addPoint` メソッドを復活させ、1件だけポイント加算
3. `setProfile` の `point` を `0` 未満なら `0` に補正

---

## 6. つまずきポイント
- `NullPointerException`
  -> インスタンス生成 (`new`) 前にアクセスしていないか確認
- `this` を使わず代入が効かない
  -> 引数名とフィールド名が同じときは `this` を付ける
- インスタンス間で値が混ざると誤解
  -> 各 `new` は別実体



