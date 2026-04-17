# Java-06 ハンズオン: 条件分岐と繰り返し

対応参考資料: `Java-06_条件分岐と繰り返し.pptx`
補講（任意）: [Java-06A switch / do-while / ラベル付き制御](./java-06a-advanced-control-flow.md)

## 1. この資料のゴール
- `if / else if / else` を使って業務条件を表現できる
- `for` と `while` を目的に応じて使い分けできる
- `break` / `continue` の基本挙動を理解する

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
1. 条件分岐は「どの処理を実行するか」を決める
2. ループは「同じ処理を繰り返す」
3. `break` はループ終了、`continue` は次の周回へ進む

---

## 4. ハンズオン

目的:
- 条件とループで業務の流れを表現する

完了条件:
- `ControlFlowDemo.java` で判定と繰り返しの両方を実行できる

作成ファイル: `~/order-management-springboot/practice/java/handson06/ControlFlowDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson06
cd ~/order-management-springboot/practice/java/handson06
```

### Step 1: if/else を使う
`ControlFlowDemo.java` を次の内容で作成:

```java
public class ControlFlowDemo { // 条件分岐の動作確認クラス
    public static void main(String[] args) { // 実行開始地点
        int stock = 8; // 現在在庫

        if (stock <= 0) { // 在庫が 0 以下なら欠品
            System.out.println("在庫なし"); // 欠品メッセージ
        } else if (stock < 10) { // 0 より大きく 10 未満なら少ない
            System.out.println("在庫少"); // 在庫少メッセージ
        } else { // それ以外は十分ある
            System.out.println("在庫あり"); // 在庫ありメッセージ
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ControlFlowDemo.java
java ControlFlowDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: for ループを追加
`ControlFlowDemo.java` を次の内容に更新:

```java
public class ControlFlowDemo { // for ループの動作確認クラス
    public static void main(String[] args) {
        for (int day = 1; day <= 5; day++) { // day を 1 から 5 まで 1 ずつ増やして繰り返す
            System.out.println("営業日: " + day + "日目"); // 各周回で現在の日数を表示
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ControlFlowDemo.java
java ControlFlowDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 3: while ループを追加
`ControlFlowDemo.java` を次の内容に更新:

```java
public class ControlFlowDemo { // while ループの動作確認クラス
    public static void main(String[] args) {
        int retry = 0; // カウンタを 0 で初期化
        while (retry < 3) { // retry が 3 未満の間は繰り返す
            System.out.println("再試行回数: " + retry); // 現在の再試行回数を表示
            retry++; // 次の周回に向けて 1 増やす（これがないと無限ループになる）
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ControlFlowDemo.java
java ControlFlowDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 4: break / continue を使う（仕上げ）
`ControlFlowDemo.java` を次の内容に更新:

```java
public class ControlFlowDemo { // break / continue の動作確認クラス
    public static void main(String[] args) {
        for (int orderNo = 1; orderNo <= 10; orderNo++) { // 注文番号 1〜10 を順に処理
            if (orderNo == 3) {
                continue; // 3番はこの周回だけ飛ばして次へ進む
            }
            if (orderNo == 8) {
                break; // 8番に到達したらループ全体を終了する
            }
            System.out.println("処理対象注文: " + orderNo); // 処理対象として出力
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 ControlFlowDemo.java
java ControlFlowDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `if` 条件を増やし、`stock` が `100` 以上なら `在庫十分` を表示する。

期待結果:
- `stock = 100` 以上で `在庫十分` が表示される。

### レベル2（拡張）
1. `for` で `1〜12` を回して偶数だけ表示する。
2. `while` を使って `countdown`（3,2,1）を表示する。

期待結果:
- 偶数表示は `2,4,6,8,10,12` のみ。
- `countdown` は `3 -> 2 -> 1` の順で終了する。

### レベル3（実務）
1. コマンドライン引数から点数を1つ受け取り、以下を順に満たすように実装する。
- 0〜100 以外なら `不正な点数です！`
- 0〜59 なら `赤点です！`
- 60〜79 なら `普通です！`
- 80〜100 なら `優秀です！`
- 100 のときだけ最後に `満点だったので宿題免除です！！`

期待結果:
- 入力 `100` では `優秀です！` と `満点だったので宿題免除です！！` の両方が表示される。
- 入力 `-1` では `不正な点数です！` が表示される。

### 実行前予想問題（1分）
次のコード片で実際に表示される注文番号を、実行前に予想してから確認してください。
- `for (int orderNo = 1; orderNo <= 6; orderNo++) { if (orderNo == 2) continue; if (orderNo == 5) break; System.out.println(orderNo); }`

### デバッグ演習（任意, 5分）
1. `if (stock <= 0)` を意図的に `if stock <= 0` に変更してコンパイルエラーを出す。
2. エラーメッセージを見て `if (条件)` 形式に修正する。
3. 再コンパイルして成功することを確認する。

---

## 6. つまずきポイント
- `if` の `()` を忘れる
  -> `if (条件)` の形を守る
- ループが止まらない
  -> `while` の更新処理（`i++` など）を確認
- `break` と `continue` を混同
  -> `break` は終了、`continue` はスキップ

