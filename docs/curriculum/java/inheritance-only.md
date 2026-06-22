# Java 継承だけガイド（extends）

## 1. この資料の目的
対象:
- `extends` の意味が曖昧な人
- 親クラスと子クラスの違いを基礎から理解したい人
- 「継承で何ができるのか」を最小コードで掴みたい人

この資料のゴール:
- 継承（`extends`）を説明できる
- 親クラスのフィールド/メソッドを子クラスで使える理由を説明できる
- 共通処理を親にまとめる設計意図を説明できる

---

## 2. 進め方（毎Step共通）
1. コードをそのまま作る
2. `javac` でコンパイルする
3. `java` で実行する
4. 期待出力と一致するか確認する
5. ずれたら `{}` / `;` / クラス名とファイル名一致を確認する

---

## 3. 継承演習（20〜30分）

### Step 0: 作業フォルダを作る
```bash
cd ~/order-management-springboot/practice/java
mkdir -p inheritance
cd inheritance
```

### Step 1: 継承の最小形（親の機能を使う）
作成ファイル: `~/order-management-springboot/practice/java/inheritance/InheritanceStep1.java`

```java
class Employee { // 親クラス（共通データと共通処理を持つ）
    String name;

    void printName() {
        System.out.println("名前: " + name);
    }
}

class FullTimeEmployee extends Employee { // 子クラス（Employeeを継承）
}

public class InheritanceStep1 {
    public static void main(String[] args) {
        FullTimeEmployee e = new FullTimeEmployee();
        e.name = "Shinesoft"; // 親クラスのフィールドを使える
        e.printName();        // 親クラスのメソッドを使える
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceStep1.java
java InheritanceStep1
```

期待出力例:
```text
名前: Shinesoft
```

コード解説:
- `extends Employee` は「Employeeの機能を引き継ぐ」という意味
- 子クラスは、親クラスで定義したメンバーを再利用できる

---

### Step 2: 子クラスに機能を追加する
作成ファイル: `~/order-management-springboot/practice/java/inheritance/InheritanceStep2.java`

```java
class Employee {
    String name;

    void printName() {
        System.out.println("名前: " + name);
    }
}

class FullTimeEmployee extends Employee {
    int monthlySalary; // 子クラス固有のデータ

    void printSalary() { // 子クラス固有の処理
        System.out.println("月給: " + monthlySalary);
    }
}

public class InheritanceStep2 {
    public static void main(String[] args) {
        FullTimeEmployee e = new FullTimeEmployee();
        e.name = "Tanaka";        // 親側のフィールド
        e.monthlySalary = 300000; // 子側のフィールド

        e.printName();   // 親側メソッド
        e.printSalary(); // 子側メソッド
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceStep2.java
java InheritanceStep2
```

期待出力例:
```text
名前: Tanaka
月給: 300000
```

コード解説:
- 継承は「親を使う」だけでなく、子に差分機能を追加できる
- 共通部分を親へ、固有部分を子へ分離すると保守しやすい

---

### Step 3: 共通化の価値を確認する（親へ集約）
作成ファイル: `~/order-management-springboot/practice/java/inheritance/InheritanceStep3.java`

```java
class Employee {
    String name;

    void printHeader() {
        System.out.println("=== 社員情報 ===");
    }
}

class FullTimeEmployee extends Employee {
    int monthlySalary;

    void printDetail() {
        printHeader(); // 親クラスの共通処理を利用
        System.out.println("名前: " + name);
        System.out.println("月給: " + monthlySalary);
    }
}

class PartTimeEmployee extends Employee {
    int hourlyWage;

    void printDetail() {
        printHeader(); // 別の子クラスでも同じ共通処理を利用
        System.out.println("名前: " + name);
        System.out.println("時給: " + hourlyWage);
    }
}

public class InheritanceStep3 {
    public static void main(String[] args) {
        FullTimeEmployee full = new FullTimeEmployee();
        full.name = "Yamada";
        full.monthlySalary = 320000;

        PartTimeEmployee part = new PartTimeEmployee();
        part.name = "Kato";
        part.hourlyWage = 1400;

        full.printDetail();
        part.printDetail();
    }
}
```

実行:
```bash
javac -encoding UTF-8 InheritanceStep3.java
java InheritanceStep3
```

期待出力例:
```text
=== 社員情報 ===
名前: Yamada
月給: 320000
=== 社員情報 ===
名前: Kato
時給: 1400
```

コード解説:
- `printHeader()` を親に置くことで、複数の子クラスで共通利用できる
- 重複コードを減らし、変更点を1か所に集約できる

---

## 4. よくあるつまずき
- `extends` を書いたのに親の機能が使えない  
  -> クラス名のスペルや定義位置を確認
- 子クラスに同名フィールドを作って混乱する  
  -> まずは親と子で責務を分ける（共通は親、固有は子）
- 継承よりコピーで実装してしまう  
  -> 共通処理は親クラスへまとめる

---

## 5. チェックリスト
- `extends` の意味を説明できる
- 親クラスのメンバーを子クラスで使える理由を説明できる
- 共通処理を親に集約するメリットを説明できる

