# Java-06A 補講: switch / do-while / ラベル付き制御

## 1. この資料のゴール
- `switch` を使って多分岐を実装できる
- `do-while` の前判定/後判定の違いを説明できる
- ネストループでラベル付き `break` を使い分けできる

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
1. `switch` は値ごとに分岐先を切り替える
2. `do-while` は1回実行してから条件判定する
3. ラベル付き `break` は外側ループまで一気に抜けられる

### 書式の基本

#### switch

```mermaid
flowchart TB
  START["判定する値"] --> SWITCH{"switch"}
  SWITCH -->|case A| CASE_A["処理A"]
  SWITCH -->|case B| CASE_B["処理B"]
  SWITCH -->|一致なし| DEFAULT["default の処理"]
  CASE_A --> BREAK["break：switch を抜ける"]
  CASE_B --> BREAK
  DEFAULT --> BREAK
  BREAK --> END["switch の後"]
```

```java
switch (判定する値) {
    case 値1:
        値1だったときの処理
        break;
    case 値2:
        値2だったときの処理
        break;
    default:
        どの case にも当てはまらないときの処理
        break;
}
```

例:

```java
String status = "PAID";

switch (status) {
    case "PAID":
        System.out.println("支払い済み");
        break;
    case "PENDING":
        System.out.println("支払い待ち");
        break;
    default:
        System.out.println("不明な状態");
        break;
}
```

ポイント:
- `switch` は1つの値を複数の候補と照合するときに使う
- `case` には一致させたい値を書く
- `default` はどの `case` にも一致しないときに実行される
- `break;` を書かないと、次の `case` の処理へ流れることがある（フォールスルー）

#### do-while

```mermaid
flowchart TB
  START["処理開始"] --> BODY["do：処理<br/>必ず1回実行"]
  BODY --> CONDITION{"while：継続条件"}
  CONDITION -->|true| BODY
  CONDITION -->|false| END["ループの後"]
```

```java
do {
    繰り返したい処理
} while (条件式);
```

例:

```java
int retry = 0;

do {
    retry++;
    System.out.println(retry);
} while (retry < 3);
```

ポイント:
- `do` の中身は、条件式に関係なく最低1回は実行される
- 条件式が `true` の間だけ繰り返す
- 最後の `while (条件式);` には `;` が必要
- 「まず1回実行してから継続判定したい」処理に向いている

#### ラベル付き break

```mermaid
flowchart TB
  OUTER["outer：外側ループ"] --> INNER["内側ループ"]
  INNER --> SELECT{"終了方法"}
  SELECT -->|break| INNER_END["内側ループだけ終了"]
  INNER_END --> OUTER
  SELECT -->|break outer| OUTER_END["内側・外側の両方を終了"]
  OUTER_END --> END["外側ループの後"]
```

```java
ラベル名:
for (...) {
    for (...) {
        break ラベル名;
    }
}
```

例:

```java
outer:
for (int row = 1; row <= 3; row++) {
    for (int col = 1; col <= 3; col++) {
        if (row == 2 && col == 2) {
            break outer;
        }
        System.out.println("row=" + row + ", col=" + col);
    }
}
```

ポイント:
- 通常の `break` は、今いる一番内側のループだけを終了する
- `break outer;` のように書くと、指定したラベルのループまで終了できる
- ラベル名は任意だが、`outer` のように外側だと分かる名前にする
- 多用すると読みづらくなるため、必要な場面だけ使う

---

## 4. ハンズオン

目的:
- 分岐と繰り返しの制御構文を拡張して扱う

完了条件:
- `AdvancedControlFlowDemo.java` で3つの構文を実行確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson06a/AdvancedControlFlowDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson06a
cd ~/order-management-springboot/practice/java/handson06a
```

### Step 1: `switch` を使った多分岐
`AdvancedControlFlowDemo.java` を次の内容で作成:

`switch` は、1つの値を複数の候補に分けて処理したいときに使う。

```java
public class AdvancedControlFlowDemo {
    public static void main(String[] args) {
        int score = 80;

        switch (score / 10) {
            case 10:
            case 9:
            case 8:
                System.out.println("評価: A");
                break;
            case 7:
            case 6:
                System.out.println("評価: B");
                break;
            default:
                System.out.println("評価: C");
                break;
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 AdvancedControlFlowDemo.java
java AdvancedControlFlowDemo
```

期待出力例:
```text
評価: A
```

### Step 2: `do-while` で後判定ループ
`AdvancedControlFlowDemo.java` を次の内容に更新:

`do-while` は、条件に関係なく最初の1回は必ず実行される。

```java
public class AdvancedControlFlowDemo {
    public static void main(String[] args) {
        int retry = 0;

        do {
            retry++;
            System.out.println("再試行: " + retry);
        } while (retry < 3);
    }
}
```

実行:
```bash
javac -encoding UTF-8 AdvancedControlFlowDemo.java
java AdvancedControlFlowDemo
```

期待出力例:
```text
再試行: 1
再試行: 2
再試行: 3
```

### Step 3: ラベル付き `break` を使う
`AdvancedControlFlowDemo.java` を次の内容に更新:

ラベル付き `break` は、ネストしたループの外側まで一気に抜けたいときに使う。

```java
public class AdvancedControlFlowDemo {
    public static void main(String[] args) {
        outer:
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (row == 2 && col == 2) {
                    break outer; // 内側だけでなく外側ループも終了
                }
                System.out.println("row=" + row + ", col=" + col);
            }
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 AdvancedControlFlowDemo.java
java AdvancedControlFlowDemo
```

期待出力例:
```text
row=1, col=1
row=1, col=2
row=1, col=3
row=2, col=1
```

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `switch` の判定対象を `String status = "PAID";` に変更し、状態別メッセージを出す。

期待状態:
- `status` に応じたメッセージが1つ表示される。

### レベル2（拡張）
1. `do-while` で `countdown`（3,2,1）を表示する。

期待状態:
- 1回以上実行される後判定ループでカウントダウンできる。

### レベル3（実務）
1. 2重ループで「不正データ検出時に処理全体を終了する」挙動をラベル付き `break` で実装する。

期待状態:
- 不正条件で外側ループまで即時終了する。

### 実行前予想問題（1分）
次のコードの出力を実行前に予想してください。
- `int n = 0; do { n++; } while (n < 0); System.out.println(n);`

### デバッグ演習（任意, 5分）
1. `switch` の `break;` を1つ外して実行する。
2. フォールスルーで想定外の分岐に流れることを確認する。
3. `break;` を戻して再実行する。

---

## 6. つまずきポイント
- `switch` で `break` 漏れ
  -> フォールスルーが意図通りか確認
- `while` と `do-while` の使い分け混乱
  -> 「先に判定」か「先に1回実行」かで選ぶ
- ラベル付き `break` の誤用
  -> どのループを抜けるかラベル名を明示する
