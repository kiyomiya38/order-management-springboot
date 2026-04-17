# Java-07A 補講: 参照型と多次元配列

対応参考資料: `J1-08_配列と参照型.pdf`, `J1-09_Stringと参照型の扱い.pdf`

## 1. この資料のゴール
- 参照型の代入で「同じ実体を指す」挙動を説明できる
- `String` の比較で `==` と `equals` を使い分けできる
- 2次元配列を走査して値を表示できる

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
1. 参照型変数は値そのものではなく参照先を保持する
2. `String` の値比較は `equals` を使う
3. 2次元配列は「配列の配列」として扱う

---

## 4. ハンズオン

目的:
- 参照型の挙動と多次元配列の操作を実行で理解する

完了条件:
- `ReferenceArrayDemo.java` で参照共有・文字列比較・2次元配列走査を確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson07a/ReferenceArrayDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson07a
cd ~/order-management-springboot/practice/java/handson07a
```

### Step 1: 参照型の代入を確認する
`ReferenceArrayDemo.java` を次の内容で作成:

```java
import java.util.Arrays;

public class ReferenceArrayDemo {
    public static void main(String[] args) {
        int[] quantitiesA = {3, 5, 2};
        int[] quantitiesB = quantitiesA; // 参照をコピー

        quantitiesB[0] = 99; // B経由で先頭要素を更新

        System.out.println("A: " + Arrays.toString(quantitiesA));
        System.out.println("B: " + Arrays.toString(quantitiesB));
    }
}
```

実行:
```bash
javac -encoding UTF-8 ReferenceArrayDemo.java
java ReferenceArrayDemo
```

期待結果:
- `A` と `B` の両方で先頭要素が `99` になる

### Step 2: `String` の比較を確認する
`ReferenceArrayDemo.java` を次の内容に更新:

```java
public class ReferenceArrayDemo {
    public static void main(String[] args) {
        String s1 = new String("PAID");
        String s2 = new String("PAID");

        System.out.println("s1 == s2: " + (s1 == s2)); // 参照比較
        System.out.println("s1.equals(s2): " + s1.equals(s2)); // 値比較
    }
}
```

実行:
```bash
javac -encoding UTF-8 ReferenceArrayDemo.java
java ReferenceArrayDemo
```

期待結果:
- `s1 == s2: false`
- `s1.equals(s2): true`

### Step 3: 2次元配列を走査する
`ReferenceArrayDemo.java` を次の内容に更新:

```java
public class ReferenceArrayDemo {
    public static void main(String[] args) {
        int[][] seats = {
                {101, 102, 103},
                {201, 202, 203},
                {301, 302, 303}
        };

        for (int row = 0; row < seats.length; row++) {
            for (int col = 0; col < seats[row].length; col++) {
                System.out.println("row=" + row + ", col=" + col + ", seatNo=" + seats[row][col]);
            }
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 ReferenceArrayDemo.java
java ReferenceArrayDemo
```

期待結果:
- 9件すべての座席番号が表示される

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `int[]` をもう1つ作り、参照コピー時と別配列時の差分を表示する。

期待結果:
- 参照コピー側は同時に変化し、別配列側は変化しない。

### レベル2（拡張）
1. `String` の比較を3パターンで確認する（`==`, `equals`, `equalsIgnoreCase`）。

期待結果:
- 参照比較と値比較の違いを説明できる。

### レベル3（実務）
1. 2次元配列の合計値を求める。
2. 行ごとの合計も別々に表示する。

期待結果:
- 全体合計と各行合計を正しく表示できる。

### 実行前予想問題（1分）
次の結果を実行前に予想してください。
- `String a = "OK"; String b = "OK"; System.out.println(a == b);`
- `String c = new String("OK"); System.out.println(a == c);`

### デバッグ演習（任意, 5分）
1. 2次元配列の内側ループを `col <= seats[row].length` に変更して実行する。
2. `ArrayIndexOutOfBoundsException` を確認する。
3. 条件を `col < seats[row].length` に戻して再実行する。

---

## 6. つまずきポイント
- `==` で文字列比較して誤判定
  -> 値比較は `equals` を使う
- 参照コピーを値コピーと誤解
  -> 配列は参照型であることを意識する
- 2次元配列で添字エラー
  -> 外側は `seats.length`、内側は `seats[row].length`
