# Java-09A 補講: CLASSPATHとパッケージ解決

対応参考資料: `J1-16_パッケージ.pdf`

## 1. この資料のゴール
- `package` とフォルダ階層の一致ルールを説明できる
- `java -cp`（クラスパス指定）で実行先を切り替えできる
- 無名パッケージと名前付きパッケージの違いを理解できる

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
1. クラスパスは「クラス探索の起点フォルダ」
2. `package app;` なら `app/` フォルダ配下に置く
3. 実務では環境変数より `-cp` 明示指定を優先する

---

## 4. ハンズオン

目的:
- コンパイル済みクラスの探索ルールを体験する

完了条件:
- `-cp` 指定の有無で実行成否が変わる理由を説明できる

作成フォルダ: `~/order-management-springboot/practice/java/handson09a`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson09a
cd ~/order-management-springboot/practice/java/handson09a
```

### Step 1: 無名パッケージ（package宣言なし）を実行する
作成ファイル: `HelloCp.java`

```java
public class HelloCp {
    public static void main(String[] args) {
        System.out.println("Hello from unnamed package");
    }
}
```

実行:
```bash
javac -encoding UTF-8 HelloCp.java
java HelloCp
```

期待結果:
- `Hello from unnamed package` が表示される

### Step 2: 名前付きパッケージを作成し `-cp` で実行する
作成フォルダ:
```bash
mkdir -p src/app out
```

作成ファイル: `src/app/CpApp.java`

```java
package app;

public class CpApp {
    public static void main(String[] args) {
        System.out.println("Hello from package app");
    }
}
```

コンパイル:
```bash
javac -encoding UTF-8 -d out src/app/CpApp.java
```

実行:
```bash
java -cp out app.CpApp
```

期待結果:
- `Hello from package app` が表示される

### Step 3: クラスパス未指定エラーを再現して修正する
意図的に失敗させる:
```bash
java app.CpApp
```

想定エラー:
- `Could not find or load main class app.CpApp`

修正実行:
```bash
java -cp out app.CpApp
```

期待結果:
- 正常に実行できる

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `CpApp` のメッセージを変更して再実行する。

期待結果:
- 変更後の文字列が表示される。

### レベル2（拡張）
1. `src/util/Printer.java` を追加し、`app.CpApp` から `import util.Printer;` で呼び出す。

期待結果:
- 複数パッケージのクラスを `-cp out` で実行できる。

### レベル3（実務）
1. `-cp out` を `-cp .` に変えた場合の成否を確認する。
2. どのパスがクラス探索の起点になっているか説明する。

期待結果:
- 失敗理由をクラス探索起点の観点で説明できる。

### 実行前予想問題（1分）
次の2コマンドのどちらが成功するか予想してください。
- `java app.CpApp`
- `java -cp out app.CpApp`

### デバッグ演習（任意, 5分）
1. `src/app/CpApp.java` の `package app;` を `package apps;` に変更してコンパイルする。
2. 実行クラス指定との不一致エラーを確認する。
3. `package` とフォルダ構成・実行コマンドを揃えて復旧する。

---

## 6. つまずきポイント
- `Could not find or load main class`
  -> `-cp` の指定先と実行クラス名（完全修飾名）を確認
- `package ... does not exist`
  -> フォルダ階層と `package` / `import` の一致を確認
- 無名パッケージと名前付きパッケージの混在
  -> 学習時以外は名前付きパッケージに揃える
