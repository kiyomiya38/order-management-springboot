# Java-13A 補講: 継承ルールの深掘り（`super` / 単一継承 / `final`）

対応参考資料: `J2_04_継承①.pdf`, `J2_05_継承②.pdf`

## 1. この資料のゴール
- 継承時のコンストラクタ呼び出し順を説明できる
- `super(...)` と暗黙 `super()` の挙動を説明できる
- 単一継承ルールと `final` 制限を説明できる

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
1. 子クラス生成時、親クラスのコンストラクタが先に動く
2. 親の引数なしコンストラクタがない場合、子で `super(...)` を明示する
3. Javaは単一継承（`extends` できる親クラスは1つ）
4. `final` メソッドはオーバーライド不可、`final` クラスは継承不可

---

## 4. ハンズオン

目的:
- 継承で起きる初期化順序と制約を実コードで確認する

完了条件:
- `InheritanceRulesDemo.java` で `super(...)` と `final` 制約を説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson13a/InheritanceRulesDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson13a
cd ~/order-management-springboot/practice/java/handson13a
```

### Step 1: コンストラクタ呼び出し順を確認する
`InheritanceRulesDemo.java` を次の内容で作成:

```java
class Parent {
    Parent() {
        System.out.println("Parent()");
    }
}

class Child extends Parent {
    Child() { // super() は暗黙に先頭で呼ばれる
        System.out.println("Child()");
    }
}

public class InheritanceRulesDemo {
    public static void main(String[] args) {
        new Child();
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceRulesDemo.java
java InheritanceRulesDemo
```

期待結果:
- `Parent()` の後に `Child()` が表示される

### Step 2: `super(...)` を明示する必要があるケース
`InheritanceRulesDemo.java` を次の内容に更新:

```java
class Parent {
    String name;

    Parent(String name) { // 引数なしコンストラクタは定義しない
        this.name = name;
        System.out.println("Parent name=" + this.name);
    }
}

class Child extends Parent {
    Child(String name) {
        super(name); // 親の引数ありコンストラクタを明示呼び出し
        System.out.println("Child ready");
    }
}

public class InheritanceRulesDemo {
    public static void main(String[] args) {
        new Child("Tanaka");
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceRulesDemo.java
java InheritanceRulesDemo
```

期待結果:
- `Parent name=Tanaka`
- `Child ready`

補足:
- `super(name);` を消すとコンパイルエラーになる（暗黙 `super()` が呼べないため）

### Step 3: 単一継承と `final` 制約を確認（仕上げ）
`InheritanceRulesDemo.java` を次の内容に更新:

```java
class Worker {
    final void submitReport() { // final メソッドは上書き不可
        System.out.println("report submitted");
    }
}

class Manager extends Worker {
    // @Override
    // void submitReport() {} // これを有効化するとコンパイルエラー
}

final class FixedRole {
}

// class DerivedRole extends FixedRole {} // final クラスは継承不可

public class InheritanceRulesDemo {
    public static void main(String[] args) {
        Manager m = new Manager();
        m.submitReport();
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceRulesDemo.java
java InheritanceRulesDemo
```

期待結果:
- `report submitted` が表示される

単一継承ルール（確認用）:

```java
// class C extends A, B {} // Javaでは不可（クラスの多重継承は不可）
```

---

## 5. ミニ演習（10分）
1. `GrandParent -> Parent -> Child` の3階層を作り、コンストラクタ順序を出力で確認する。
2. `Parent` に引数なしコンストラクタを追加し、`Child` の `super(...)` 省略時の挙動を確認する。
3. `final` メソッドを通常メソッドへ変更し、オーバーライドが可能になることを確認する。

---

## 6. つまずきポイント
- 親に引数なしコンストラクタがないのに `super(...)` を書かない
  -> 子コンストラクタ先頭で明示呼び出しする
- `super(...)` の前に処理を書く
  -> `super(...)` はコンストラクタ先頭のみ
- `final` の意味を「変数だけ」と誤解
  -> メソッド/クラスにも適用できる
