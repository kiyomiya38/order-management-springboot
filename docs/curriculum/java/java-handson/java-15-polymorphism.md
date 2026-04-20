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

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- 例: `17.0.x`

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
class Employee { // 親クラス
    String name; // 社員名

    String roleLabel() { // 役割ラベル（親の既定値）
        return "社員";
    }
}

class Manager extends Employee { // 子クラス1
    @Override
    String roleLabel() { // 役割ラベルを上書き
        return "管理者";
    }
}

class PartTimer extends Employee { // 子クラス2
    @Override
    String roleLabel() { // 役割ラベルを上書き
        return "アルバイト";
    }
}

public class PolymorphismDemo { // 実行クラス
    static void printRole(Employee e) { // 親型で受け取る共通メソッド
        System.out.println(e.name + " は " + e.roleLabel()); // 実体に応じた roleLabel が呼ばれる
    }

    public static void main(String[] args) {
        Employee m = new Manager(); // 親型変数に Manager 実体を代入
        m.name = "Yamada"; // 名前設定

        Employee p = new PartTimer(); // 親型変数に PartTimer 実体を代入
        p.name = "Kato"; // 名前設定

        printRole(m); // Manager 実装で表示
        printRole(p); // PartTimer 実装で表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 PolymorphismDemo.java
java PolymorphismDemo
```

期待出力例:
```text
Yamada は 管理者
Kato は アルバイト
```



### Step 2: `instanceof` で型判定する
`PolymorphismDemo.java` を次の内容に更新:

```java
class Employee { // 親クラス
    String name; // 共通フィールド
}

class Manager extends Employee { // 子クラス
    String teamName; // 子クラス固有フィールド
}

public class PolymorphismDemo { // 実行クラス
    public static void main(String[] args) {
        Employee e = new Manager(); // 親型で保持（実体は Manager）
        e.name = "Tanaka"; // 親側の共通フィールドへ代入

        if (e instanceof Manager) { // 実体が Manager かを先に確認
            Manager m = (Manager) e; // 判定後に安全にダウンキャスト
            m.teamName = "Platform"; // 子クラス固有フィールドへ代入
            System.out.println(m.name + " / " + m.teamName); // 値を表示
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 PolymorphismDemo.java
java PolymorphismDemo
```

期待出力例:
```text
Tanaka / Platform
```



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



