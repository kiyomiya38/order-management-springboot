# Lesson1: クラス・オブジェクト・フィールド・メソッド

## 1. 到達目標
- 「クラス」「オブジェクト」を自分の言葉で説明できる
- `new` でオブジェクトを作り、フィールドに値を入れてメソッドを呼べる
- 実行結果を見て「なぜその結果か」を説明できる

---

## 2. 先に用語を最短で理解する
- クラス: 設計図（どんなデータと処理を持つかの定義）
- オブジェクト: 設計図から作られた実体（`new` で作る）
- フィールド: オブジェクトが持つデータ
- メソッド: オブジェクトが持つ処理

---

## 3. 実施手順（コマンド）
```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/phase1/lesson1
cd ~/order-management-springboot/practice/phase1/lesson1
```

`Lesson1Main.java` を作成し、次のコードを貼り付けます。

```java
class User {
    String employeeCode;
    String name;
    String department;

    String displayName() {
        return employeeCode + " " + name + " (" + department + ")";
    }
}

public class Lesson1Main {
    public static void main(String[] args) {
        User user = new User();
        user.employeeCode = "U001";
        user.name = "Yamada";
        user.department = "Sales";

        String text = user.displayName();
        System.out.println(text);
    }
}
```

実行:
```bash
javac Lesson1Main.java
java Lesson1Main
```

期待結果:
```text
U001 Yamada (Sales)
```

---

## 4. 1行ずつ説明（ここが重要）
1. `class User {`
   - `User` という設計図（クラス）を作る宣言です。
2. `String employeeCode;`
   - 従業員コードを入れる箱（フィールド）です。
3. `String name;`
   - 名前を入れる箱です。
4. `String department;`
   - 部署名を入れる箱です。
5. `String displayName() {`
   - ユーザー表示文字列を作る処理（メソッド）です。
6. `return employeeCode + " " + name + " (" + department + ")";`
   - フィールドの値を結合して文字列を返します。
7. `User user = new User();`
   - `User` クラスから実体（オブジェクト）を1個作っています。
8. `user.employeeCode = "U001";`
   - 作ったオブジェクトの従業員コードに値を入れます。
9. `user.name = "Yamada";`
   - 名前に値を入れます。
10. `user.department = "Sales";`
    - 部署に値を入れます。
11. `String text = user.displayName();`
    - オブジェクトのメソッドを呼び出し、戻り値を受け取ります。
12. `System.out.println(text);`
    - 受け取った文字列を画面に表示します。

---

## 5. 演習

### 演習A（必須）
`main` に `user2` を追加し、以下を表示してください。
- `U002 Suzuki (HR)`

### 演習B（必須）
`displayName()` の戻り値を次の形式に変更してください。
- `[U001] Yamada - Sales`

### 演習C（任意）
`department` を `"General Affairs"` にしたら表示がどう変わるか、実行前に予想してから実行してください。

---

## 6. 自己チェック（口頭で説明できれば合格）
1. クラスとオブジェクトの違いは？
2. `new User()` は何をしている？
3. フィールドとメソッドは何が違う？
4. `user.displayName()` を実行すると、なぜ文字列が返る？

---

## 7. チェック用の回答例（講師確認用）
1. クラスは設計図、オブジェクトはその実体。
2. `User` 型の実体を1つメモリ上に作る。
3. フィールドはデータ、メソッドは処理。
4. `displayName()` 内の `return` が文字列を呼び出し元へ返しているため。

---

## 8. 詰まった時の観点
- `cannot find symbol`:
  - 変数名やメソッド名のスペルを確認する
- 実行結果が想定と違う:
  - `System.out.println(...)` で途中の値を表示して確認する
- 何をしているかわからない:
  - 1行ずつ「この行の入力は何か」「出力は何か」で分解する
