# Java-01 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-01-intro.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
直前のハンズオン完成コードから、次の式を変更する。その他のコードは残す。
変更内容:
- `検証対象件数` の式を `10 + 20 * 3` に変更

```java
public class IntroHello {
    public static void main(String[] args) {
        System.out.println("受注バッチ開始");
        System.out.println("検証対象件数: " + (10 + 20 * 3));
        System.out.println("受注バッチ終了");
    }
}
```

期待出力例:
```text
受注バッチ開始
検証対象件数: 70
受注バッチ終了
```

---

## レベル2（拡張）解答
レベル1の完成コードを引き継ぎ、クラス名の変更と出力行の追加を行う。
変更内容:
1. `System.out.println(...)` を1行追加
2. クラス名を `StartApp` に変更（ファイル名も `StartApp.java`）

`StartApp.java`:
```java
public class StartApp {
    public static void main(String[] args) {
        System.out.println("受注バッチ開始");
        System.out.println("検証対象件数: " + (10 + 20 * 3));
        System.out.println("処理ステップ2を実行");
        System.out.println("受注バッチ終了");
    }
}
```

実行コマンド:
```bash
javac -encoding UTF-8 StartApp.java
java StartApp
```

---

## レベル3（実務）解答
レベル2の完成コードを引き継ぎ、すべてのログへ`[INFO]`を追加する。
変更内容:
- 各ログ文の先頭に `[INFO]` を付与
- 表示順序は「開始 -> 件数表示 -> 追加した処理 -> 終了」を維持

```java
public class StartApp {
    public static void main(String[] args) {
        System.out.println("[INFO] 受注バッチ開始");
        System.out.println("[INFO] 検証対象件数: " + (10 + 20 * 3));
        System.out.println("[INFO] 処理ステップ2を実行");
        System.out.println("[INFO] 受注バッチ終了");
    }
}
```

期待出力例:
```text
[INFO] 受注バッチ開始
[INFO] 検証対象件数: 70
[INFO] 処理ステップ2を実行
[INFO] 受注バッチ終了
```

---

## 実行前予想問題の解答
1. `System.out.println(4 + 5 * 6);`
- 出力: `34`

2. `System.out.println((4 + 5) * 6);`
- 出力: `54`

---

## デバッグ演習（任意）の解答
手順:
1. `System.out.println("受注バッチ開始");` の `;` を削除してコンパイル
2. `';' expected` エラーを確認
3. `;` を戻して再コンパイルし成功を確認

補足:
- Javaのコンパイルエラーは「行番号」と「エラーメッセージ」をセットで確認すると修正しやすい
