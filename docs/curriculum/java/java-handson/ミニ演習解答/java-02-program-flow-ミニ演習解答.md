# Java-02 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-02-program-flow.md`

## レベル1（基本）解答
変更内容:
- `System.out.println` を1行追加して処理ステップを増やす

```java
public class HelloFlow {
    public static void main(String[] args) {
        System.out.println("[INFO] バッチ起動");
        System.out.println("[INFO] 設定ファイル読込");
        System.out.println("[INFO] 受注データ読込");
        System.out.println("[INFO] バッチ正常終了");
    }
}
```

期待出力例:
```text
[INFO] バッチ起動
[INFO] 設定ファイル読込
[INFO] 受注データ読込
[INFO] バッチ正常終了
```

---

## レベル2（拡張）解答
変更内容:
1. `main` の引数名 `args` を `parameters` に変更
2. インデントを整えて可読性を維持

```java
public class HelloFlow {
    public static void main(String[] parameters) {
        System.out.println("[INFO] バッチ起動");
        System.out.println("[INFO] 受注データ読込");
        System.out.println("[INFO] バッチ正常終了");
    }
}
```

補足:
- `main` の引数名は任意のため、`parameters` でも動作する。
- 重要なのは型と形（`public static void main(String[] ...)`）で、変数名ではない。

---

## レベル3（実務）解答
変更内容:
- ログを「開始」「コンパイル対象クラス名」「終了」の3行に整理
- 並び順を「開始 -> 処理内容 -> 終了」に固定

```java
public class HelloFlow {
    public static void main(String[] args) {
        System.out.println("[INFO] 開始");
        System.out.println("[INFO] コンパイル対象クラス名: HelloFlow");
        System.out.println("[INFO] 終了");
    }
}
```

期待出力例:
```text
[INFO] 開始
[INFO] コンパイル対象クラス名: HelloFlow
[INFO] 終了
```

---

## 実行前予想問題の解答
対象コマンド:
- `java HelloFlow`
- `java HelloFlow.java`

違い:
1. `java HelloFlow`
- 事前に `javac HelloFlow.java` でコンパイル済みの `HelloFlow.class` を実行する。

2. `java HelloFlow.java`
- ソースファイル起動（single-file source-code mode）。
- `javac` を明示しなくても、`java` コマンドがソースを読み込んで実行する。

JDK 17での学習方針:
- 本カリキュラムでは「作成 -> コンパイル -> 実行」を体得するため、基本は  
  `javac ...` -> `java クラス名` を使う。

---

## デバッグ演習（任意）の解答
手順:
1. `HelloFlow.java` 内のクラス名だけ `HelloFlowApp` に変更してコンパイル
2. 以下の不一致エラーを確認  
   `class HelloFlowApp is public, should be declared in a file named HelloFlowApp.java`
3. 次のいずれかで修正
- クラス名を `HelloFlow` に戻す
- ファイル名を `HelloFlowApp.java` に変更する

補足:
- `public` クラスは「クラス名とファイル名の一致」が必須。
