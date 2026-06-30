# Java-16A 補講: 正規表現の基礎（メタ文字とエスケープ）

## 1. この資料のゴール
- 正規表現の主要メタ文字（`.` `*` `+` `?` `[]` `()`）を説明できる
- Java文字列内でのエスケープ（`\\d` や `\\.`）を正しく書ける
- `Pattern` / `Matcher` を使って入力チェックを実装できる

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
1. 正規表現は「文字列パターン」を表す
2. Java文字列では `\` 自体もエスケープが必要（例: `\d` は `"\\d"`）
3. `matches()` は「文字列全体一致」、`find()` は「部分一致検索」

代表メタ文字（最小セット）:

| 記法 | 意味 | 例 |
|---|---|---|
| `.` | 任意1文字 | `a.c` |
| `*` | 直前0回以上 | `ab*c` |
| `+` | 直前1回以上 | `ab+c` |
| `?` | 直前0回または1回 | `colou?r` |
| `[]` | 文字クラス | `[A-Z]` |
| `()` | グループ化 | `(abc)+` |
| `^` / `$` | 先頭 / 末尾 | `^[0-9]+$` |

---

## 4. ハンズオン

目的:
- エスケープを含む正規表現を実コードで扱えるようにする

完了条件:
- `RegexBasicsDemo.java` で `matches` と `find` の違いを確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson16a/RegexBasicsDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson16a
cd ~/order-management-springboot/practice/java/handson16a
```

### Step 1: `matches()` で全体一致を確認する
`RegexBasicsDemo.java` を次の内容で作成:

```java
public class RegexBasicsDemo { // 正規表現の基本確認クラス
    public static void main(String[] args) {
        String zip = "123-4567"; // 郵便番号形式（7桁）
        boolean ok = zip.matches("^\\d{3}-\\d{4}$"); // 全体一致で形式チェック

        System.out.println("zip=" + zip); // 入力値
        System.out.println("形式OK=" + ok); // true 期待
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 RegexBasicsDemo.java
java RegexBasicsDemo
```

期待出力例:
```text
zip=123-4567
形式OK=true
```

### Step 2: `Pattern` / `Matcher` で部分一致を探す
`RegexBasicsDemo.java` を次の内容に更新:

```java
import java.util.regex.Matcher; // 検索結果を扱う
import java.util.regex.Pattern; // 正規表現パターンを扱う

public class RegexBasicsDemo {
    public static void main(String[] args) {
        String text = "order-2026-0420 paid"; // 検索対象
        Pattern pattern = Pattern.compile("\\d{4}-\\d{4}"); // 4桁-4桁を探す
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) { // 部分一致を検索
            System.out.println("見つかったID: " + matcher.group()); // order番号の数字部
        } else {
            System.out.println("見つかりません");
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 RegexBasicsDemo.java
java RegexBasicsDemo
```

期待出力例:
```text
見つかったID: 2026-0420
```

### Step 3: エスケープ必須パターンを試す（仕上げ）
`RegexBasicsDemo.java` を次の内容に更新:

```java
public class RegexBasicsDemo {
    public static void main(String[] args) {
        String fileName = "report.v1.csv";
        boolean csv = fileName.matches("^.+\\.csv$"); // . は任意文字なので \\.

        String amount = "12,500";
        boolean money = amount.matches("^\\d{1,3}(,\\d{3})*$"); // 3桁カンマ区切り

        System.out.println("csv判定: " + csv); // true 期待
        System.out.println("金額判定: " + money); // true 期待
    }
}
```

実行:
```bash
javac -encoding UTF-8 RegexBasicsDemo.java
java RegexBasicsDemo
```

期待出力例:
```text
csv判定: true
金額判定: true
```

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `user@example.com` を簡易チェックする正規表現を作る（厳密すぎなくてよい）。

期待出力例:
```text
メール形式: true
```

### レベル2（拡張）
1. `find()` で `text = "A12 B34 C56"` から2桁数字を順に取り出す。

期待出力例:
```text
12
34
56
```

### レベル3（実務）
1. `^` と `$` を外した場合の判定差を確認する。

期待状態:
- 文字列全体一致と部分一致の違いを説明できる

---

## 6. つまずきポイント
- `\d` と書いてコンパイルエラー
  -> Java文字列では `"\\d"` と書く
- `.` をそのまま使って想定外に一致
  -> リテラルのドットは `"\\."`
- `matches()` で部分一致したい
  -> 部分一致は `find()` を使う
