# Java-13A 補講: 継承ルールの深掘り（`super` / 単一継承 / `final`）

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
2. `super(...)` は「親クラスのコンストラクタを呼ぶ命令」
3. 親の引数なしコンストラクタがない場合、子で `super(...)` を明示する
4. Javaは単一継承（`extends` できる親クラスは1つ）
5. `final` メソッドはオーバーライド不可、`final` クラスは継承不可

`super(...)` の意味と必要性（初学者向け）:
- 子クラスのインスタンスには「親クラスの部分」も含まれるため、親側の初期化が先に必要。
- `super(...)` は、親側の初期化に必要な値を渡して「親の準備を先に終える」ために使う。
- 親に引数なしコンストラクタがある場合は、`super()` が自動で入る（暗黙 `super()`）。
- 親に引数なしコンストラクタがない場合、暗黙 `super()` は失敗するため `super(値)` を自分で書く必要がある。
- `super(...)` はコンストラクタの先頭にしか書けない。

初期化の流れ（例）:
`new Child("Tanaka")` -> `super("Tanaka")` -> 親の初期化 -> 子の初期化

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
class Parent { // 親クラス: 子クラスから継承される側
    Parent() { // 親クラスの引数なしコンストラクタ
        System.out.println("Parent()"); // 親の初期化が先に動くことを表示
    } // 親コンストラクタの終わり
} // Parent クラス定義の終わり

class Child extends Parent { // 子クラス: Parent を継承
    Child() { // 子クラスの引数なしコンストラクタ
        // ここで super() が暗黙に先頭呼び出しされてから、この行が実行される
        System.out.println("Child()"); // 子の初期化処理が後で動くことを表示
    } // 子コンストラクタの終わり
} // Child クラス定義の終わり

public class InheritanceRulesDemo { // 実行クラス
    public static void main(String[] args) { // プログラム開始地点
        new Child(); // Child 生成時に「親 -> 子」の順でコンストラクタが実行される
    } // main メソッドの終わり
} // 実行クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 InheritanceRulesDemo.java
java InheritanceRulesDemo
```

期待出力例:
```text
Parent()
Child()
```

### Step 2: `super(...)` を明示する必要があるケース
`InheritanceRulesDemo.java` を次の内容に更新:

```java
class Parent { // 親クラス
    String name; // 親クラスが保持する名前フィールド

    Parent(String name) { // 引数ありコンストラクタ（引数なしコンストラクタは定義しない）
        this.name = name; // 受け取った名前を親フィールドへ保存
        System.out.println("Parent name=" + this.name); // 親側の初期化結果を表示
    } // 親コンストラクタの終わり
} // Parent クラス定義の終わり

class Child extends Parent { // 子クラス: Parent を継承
    Child(String name) { // 子クラスの引数ありコンストラクタ
        super(name); // 先頭で親コンストラクタを明示呼び出し（name を親へ渡す）
        System.out.println("Child ready"); // 子側の初期化完了を表示
    } // 子コンストラクタの終わり
} // Child クラス定義の終わり

public class InheritanceRulesDemo { // 実行クラス
    public static void main(String[] args) { // プログラム開始地点
        new Child("Tanaka"); // Parent(String) -> Child(String) の順で初期化される
    } // main メソッドの終わり
} // 実行クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 InheritanceRulesDemo.java
java InheritanceRulesDemo
```

期待出力例:
```text
Parent name=Tanaka
Child ready
```

補足:
- `super(name);` を消すと、コンパイラは暗黙 `super()` を入れようとするが、親に引数なしコンストラクタがないためコンパイルエラーになる

### Step 3: 単一継承と `final` 制約を確認（仕上げ）
`InheritanceRulesDemo.java` を次の内容に更新:

```java
class Worker { // 親クラス
    final void submitReport() { // final メソッド: 子クラスで上書き（override）できない
        System.out.println("report submitted"); // レポート提出完了メッセージを表示
    } // submitReport メソッドの終わり
} // Worker クラス定義の終わり

class Manager extends Worker { // 子クラス: Worker を継承
    // 任意確認: 下の2行は「final メソッドの上書きエラー」を確認するときだけコメント解除する
    // @Override // 親メソッドを上書きしていることを明示するアノテーション
    // void submitReport() {} // final メソッドを上書きしようとしてコンパイルエラーになる
} // Manager クラス定義の終わり

final class FixedRole { // final クラス: このクラス自体を継承できない
} // FixedRole クラス定義の終わり

// class DerivedRole extends FixedRole {} // final クラスを extends するとコンパイルエラー

public class InheritanceRulesDemo { // 実行クラス
    public static void main(String[] args) { // プログラム開始地点
        Manager m = new Manager(); // Manager のインスタンスを生成
        m.submitReport(); // 親クラスの final メソッドをそのまま利用する
    } // main メソッドの終わり
} // 実行クラス定義の終わり
```

任意確認（`final` 上書きエラーを体験したい場合）:
1. `Manager` 内の `@Override` と `void submitReport() {}` の2行をコメント解除する。
2. `javac -encoding UTF-8 InheritanceRulesDemo.java` を実行する。
3. `final` メソッドはオーバーライドできない旨のコンパイルエラーを確認する。
4. 確認後は2行を再びコメントアウトして、次の通常実行へ進む。

任意確認（`final` クラスの継承エラーを体験したい場合）:
1. 次の行のコメントアウトを一時的に外す。

```java
class DerivedRole extends FixedRole {}
```

2. `javac -encoding UTF-8 InheritanceRulesDemo.java` を実行する。

期待状態:
- `cannot inherit from final FixedRole` のようなコンパイルエラーになる
- `final` クラスは親クラスとして継承できないことが分かる

3. 確認後は、次のように再びコメントアウトして元に戻す。

```java
// class DerivedRole extends FixedRole {} // final クラスを extends するとコンパイルエラー
```

実行:
```bash
javac -encoding UTF-8 InheritanceRulesDemo.java
java InheritanceRulesDemo
```

期待出力例:
```text
report submitted
```

単一継承ルール（確認用）:

```java
// class A {} // 1つ目の親候補クラス
// class B {} // 2つ目の親候補クラス
// class C extends A, B {} // Javaでは不可: クラスの多重継承はサポートされない
```

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `GrandParent -> Parent -> Child` の3階層を作り、コンストラクタ順序を出力で確認する。

期待出力例:
```text
GrandParent
Parent
Child
```

### レベル2（拡張）
1. `Parent` に引数なしコンストラクタを追加し、`Child` の `super(...)` 省略時の挙動を確認する。

期待状態:
- `super(...)` を明示しなくても、親の引数なしコンストラクタが呼ばれる

### レベル3（実務）
1. `final` メソッドを通常メソッドへ変更し、オーバーライドが可能になることを確認する。

期待状態:
- `@Override` を付けた子クラス側メソッドがコンパイルできる

---

## 6. つまずきポイント
- 親に引数なしコンストラクタがないのに `super(...)` を書かない
  -> 子コンストラクタ先頭で明示呼び出しする
- `super(...)` の前に処理を書く
  -> `super(...)` はコンストラクタ先頭のみ
- `final` の意味を「変数だけ」と誤解
  -> メソッド/クラスにも適用できる
