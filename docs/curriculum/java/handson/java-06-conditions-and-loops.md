# Java-06 ハンズオン: 条件分岐と繰り返し

対応参考資料: `Java-06_条件分岐と繰り返し.pptx`

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
public class ControlFlowDemo {
    public static void main(String[] args) {
        int stock = 8;

        if (stock <= 0) {
            System.out.println("在庫なし");
        } else if (stock < 10) {
            System.out.println("在庫少");
        } else {
            System.out.println("在庫あり");
        }
    }
}
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
public class ControlFlowDemo {
    public static void main(String[] args) {
        for (int day = 1; day <= 5; day++) {
            System.out.println("営業日: " + day + "日目");
        }
    }
}
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
public class ControlFlowDemo {
    public static void main(String[] args) {
        int retry = 0;
        while (retry < 3) {
            System.out.println("再試行回数: " + retry);
            retry++;
        }
    }
}
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
public class ControlFlowDemo {
    public static void main(String[] args) {
        for (int orderNo = 1; orderNo <= 10; orderNo++) {
            if (orderNo == 3) {
                continue; // 3番はスキップ
            }
            if (orderNo == 8) {
                break; // 8番で終了
            }
            System.out.println("処理対象注文: " + orderNo);
        }
    }
}
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
1. `if` 条件を増やし、`stock` が `100` 以上なら `在庫十分` を表示
2. `for` で `1〜12` を回して偶数だけ表示
3. `while` を使って `countdown`（3,2,1）を表示

---

## 6. つまずきポイント
- `if` の `()` を忘れる
  -> `if (条件)` の形を守る
- ループが止まらない
  -> `while` の更新処理（`i++` など）を確認
- `break` と `continue` を混同
  -> `break` は終了、`continue` はスキップ
