# Java-13 ハンズオン: 継承

対応参考資料: `Java-13_継承.pptx`

## 1. この資料のゴール
- `extends` を使った継承を実装できる
- 親クラスと子クラスの役割分担を説明できる
- オーバーライドの基本を理解できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. 継承は共通処理を親へ集約する仕組み
2. 子クラスは親の機能を再利用できる
3. 同名メソッドを子で再定義するのがオーバーライド

---

## 4. ハンズオン

目的:
- 共通化と差分実装を体験する

完了条件:
- `InheritanceDemo.java` で親・子クラスの動作を確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson13/InheritanceDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson13
cd ~/order-management-springboot/practice/java/handson13
```

### Step 1: 親クラスと子クラスを作る
`InheritanceDemo.java` を次の内容で作成:

```java
class Employee {
    String name;

    void printName() {
        System.out.println("名前: " + name);
    }
}

class Manager extends Employee {
}

public class InheritanceDemo {
    public static void main(String[] args) {
        Manager m = new Manager();
        m.name = "Tanaka";
        m.printName();
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceDemo.java
java InheritanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: 子クラスへ機能追加
`InheritanceDemo.java` を次の内容に更新:

```java
class Employee {
    String name;

    void printName() {
        System.out.println("名前: " + name);
    }
}

class Manager extends Employee {
    String teamName;

    void printTeam() {
        System.out.println("チーム: " + teamName);
    }
}

public class InheritanceDemo {
    public static void main(String[] args) {
        Manager m = new Manager();
        m.name = "Tanaka";
        m.teamName = "Platform";
        m.printName();
        m.printTeam();
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceDemo.java
java InheritanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: オーバーライドする（仕上げ）
`InheritanceDemo.java` を次の内容に更新:

```java
class Employee {
    String name;

    String roleLabel() {
        return "社員";
    }

    void printProfile() {
        System.out.println(roleLabel() + ": " + name);
    }
}

class Manager extends Employee {
    @Override
    String roleLabel() {
        return "管理者";
    }
}

public class InheritanceDemo {
    public static void main(String[] args) {
        Manager m = new Manager();
        m.name = "Tanaka";
        m.printProfile();
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceDemo.java
java InheritanceDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `PartTimeEmployee` クラスを追加
2. `roleLabel()` をそれぞれ上書き
3. 出力を「役割: 名前」の形式で統一

---

## 6. つまずきポイント
- `@Override` エラー
  -> 親とメソッド名・引数・戻り値が一致しているか確認
- 親にないフィールドへアクセス
  -> クラス定義の責務を整理
- 継承しすぎて複雑化
  -> 共通化が明確な場合に限定する
