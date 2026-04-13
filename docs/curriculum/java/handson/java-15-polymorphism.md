# Java-15 ハンズオン: 多態性（ポリモーフィズム）

対応参考資料: `Java-15_多態性.pptx`

## 1. この資料のゴール
- 親型で受ける設計のメリットを説明できる
- 実体に応じてメソッド実装が切り替わることを確認できる
- `instanceof` を使った安全な型判定を実装できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. 変数型が親でも、実体が子なら子の実装が呼ばれる
2. これにより呼び出し側のコードを共通化できる
3. ダウンキャストは `instanceof` で判定してから行う

---

## 4. ハンズオン

目的:
- 多態性の挙動を実行で確認する

完了条件:
- `PolymorphismDemo.java` で複数実装を同じ処理で扱える

作成ファイル: `~/order-management-springboot/practice/java/handson15/PolymorphismDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson15
cd ~/order-management-springboot/practice/java/handson15
```

### Step 1: 親型で受ける
`PolymorphismDemo.java` を次の内容で作成:

```java
class Employee {
    String name;

    String roleLabel() {
        return "社員";
    }
}

class Manager extends Employee {
    @Override
    String roleLabel() {
        return "管理者";
    }
}

class PartTimer extends Employee {
    @Override
    String roleLabel() {
        return "アルバイト";
    }
}

public class PolymorphismDemo {
    static void printRole(Employee e) {
        System.out.println(e.name + " は " + e.roleLabel());
    }

    public static void main(String[] args) {
        Employee m = new Manager();
        m.name = "Yamada";

        Employee p = new PartTimer();
        p.name = "Kato";

        printRole(m);
        printRole(p);
    }
}
```

実行:
```bash
javac -encoding UTF-8 PolymorphismDemo.java
java PolymorphismDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: `instanceof` で型判定する
`PolymorphismDemo.java` を次の内容に更新:

```java
class Employee {
    String name;
}

class Manager extends Employee {
    String teamName;
}

public class PolymorphismDemo {
    public static void main(String[] args) {
        Employee e = new Manager();
        e.name = "Tanaka";

        if (e instanceof Manager) {
            Manager m = (Manager) e; // 安全にダウンキャスト
            m.teamName = "Platform";
            System.out.println(m.name + " / " + m.teamName);
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 PolymorphismDemo.java
java PolymorphismDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `Engineer` クラスを追加して `printRole` で表示
2. `instanceof` 分岐を増やして型ごとの表示を変える
3. 共通メソッドだけで処理できるように設計を見直す

---

## 6. つまずきポイント
- キャストで `ClassCastException`
  -> `instanceof` 判定を先に行う
- 親型で子固有メソッドを直接呼んでエラー
  -> 親型参照では親に定義されたメソッドのみ呼べる
- 多態性のメリットが見えない
  -> 呼び出し側の `if` 分岐削減に着目
