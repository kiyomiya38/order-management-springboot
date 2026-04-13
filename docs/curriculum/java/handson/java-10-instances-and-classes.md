# Java-10 ハンズオン: インスタンスとクラス

対応参考資料: `Java-10_インスタンスとクラス.pptx`

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

---

## 3. 先に覚えるポイント
1. クラスは設計図、インスタンスは実体
2. インスタンスごとにフィールド値を持つ
3. `this` は「今このインスタンス自身」

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
class Customer {
    String name;
    int point;
}

public class InstanceDemo {
    public static void main(String[] args) {
        Customer c1 = new Customer();
        c1.name = "Tanaka";
        c1.point = 120;

        Customer c2 = new Customer();
        c2.name = "Suzuki";
        c2.point = 80;

        System.out.println(c1.name + " point: " + c1.point);
        System.out.println(c2.name + " point: " + c2.point);
    }
}
```

実行:
```bash
javac -encoding UTF-8 InstanceDemo.java
java InstanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: メソッドを追加する
`InstanceDemo.java` を次の内容に更新:

```java
class Customer {
    String name;
    int point;

    void addPoint(int value) {
        point += value;
    }
}

public class InstanceDemo {
    public static void main(String[] args) {
        Customer c1 = new Customer();
        c1.name = "Tanaka";
        c1.point = 120;
        c1.addPoint(30);

        Customer c2 = new Customer();
        c2.name = "Suzuki";
        c2.point = 80;

        System.out.println(c1.name + " point: " + c1.point);
        System.out.println(c2.name + " point: " + c2.point);
    }
}
```

実行:
```bash
javac -encoding UTF-8 InstanceDemo.java
java InstanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: `this` を使う（仕上げ）
`InstanceDemo.java` を次の内容に更新:

```java
class Customer {
    String name;
    int point;

    void setProfile(String name, int point) {
        this.name = name;   // 引数名とフィールド名を区別
        this.point = point;
    }
}

public class InstanceDemo {
    public static void main(String[] args) {
        Customer c1 = new Customer();
        c1.setProfile("Tanaka", 120);

        Customer c2 = new Customer();
        c2.setProfile("Suzuki", 80);

        System.out.println(c1.name + " point: " + c1.point);
        System.out.println(c2.name + " point: " + c2.point);
    }
}
```

実行:
```bash
javac -encoding UTF-8 InstanceDemo.java
java InstanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


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
