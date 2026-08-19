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

### 正規表現とJava文字列を2段階で読む

```java
String regex = "\\d{3}-\\d{4}";
```

コード上では`\\d`と書きますが、正規表現へ渡る文字は`\d`です。

```text
Javaコード   "\\d"
    ↓ Java文字列として解釈
正規表現     \d
    ↓ 正規表現として解釈
意味         数字1文字
```

`matches()`と`find()`は、同じパターンでも確認範囲が異なります。

| 呼び出し | 判定する範囲 | 例 |
| --- | --- | --- |
| `text.matches(regex)` | 文字列全体 | 郵便番号の形式チェック |
| `matcher.find()` | 文字列の一部分 | 文中から注文番号を探す |

形式チェックには全体一致、長い文章から検索する場合は部分一致、と使い分けます。

`Pattern`と`Matcher`を使う検索は、次の4段階で読みます。

| 呼び出し | 役割 |
| --- | --- |
| `Pattern.compile(...)` | 正規表現から検索ルールを作る |
| `pattern.matcher(text)` | どの文字列を検索するか設定する |
| `matcher.find()` | 検索ルールに一致する部分があるか探す |
| `matcher.group()` | 実際に見つかった文字列を取得する |

```text
正規表現から検索ルールを作る
  ↓ Pattern.compile(...)
検索対象の文字列を設定する
  ↓ pattern.matcher(text)
一致する部分を探す
  ↓ matcher.find()
見つかった文字列を取得する
  ↓ matcher.group()
```

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
// ===== Step 2 で追加・変更 =====
import java.util.regex.Matcher; // 検索結果を扱う
import java.util.regex.Pattern; // 正規表現パターンを扱う

public class RegexBasicsDemo {
    public static void main(String[] args) {
        String text = "order-2026-0420 paid"; // 検索対象
        // Pattern.compile(...)で「4桁-4桁」という検索ルールを作る
        Pattern pattern = Pattern.compile("\\d{4}-\\d{4}");

        // pattern.matcher(text)で、検索対象をtextに設定する
        Matcher matcher = pattern.matcher(text);

        // find()で、検索ルールに一致する部分があるかを探す
        if (matcher.find()) {
            // group()で、実際に見つかった文字列を取得する
            String foundText = matcher.group();
            System.out.println("見つかったID: " + foundText);
        } else {
            System.out.println("見つかりません");
        }
    }
}
// ===== Step 2 で追加・変更ここまで =====
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
// ===== Step 3 で追加・変更 =====
public class RegexBasicsDemo {
    public static void main(String[] args) {
        String fileName = "report.v1.csv";
        boolean csv = fileName.matches("^.+\\.csv$"); // . は任意文字なので \\.

        String amount = "12,500";
        boolean money = amount.matches("^\\d{1,3}(,\\d{3})*$"); // 3桁カンマ区切り

        System.out.println("csv判定: " + csv); // true 期待
        System.out.println("金額判定: " + money); // true 期待
// ===== Step 3 で追加・変更ここまで =====
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

### Step 4: 全体一致と部分一致を1つのコードにまとめる
ミニ演習では、このコードに正規表現を追加・変更します。`RegexBasicsDemo.java` を次の内容に更新:

```java
// ===== Step 4 で追加・変更 =====
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexBasicsDemo {
    public static void main(String[] args) {
        String email = "user@example.com";
        Pattern emailPattern = Pattern.compile("^$"); // 演習で簡易メール形式へ変更する
        System.out.println("メール形式: " + emailPattern.matcher(email).matches());

        String text = "order-2026-0420 paid";
        Pattern numberPattern = Pattern.compile("\\d{4}-\\d{4}");
        Matcher numberMatcher = numberPattern.matcher(text);
        if (numberMatcher.find()) {
            System.out.println("見つかったID: " + numberMatcher.group());
        }
// ===== Step 4 で追加・変更ここまで =====
    }
}
```

期待出力例:
```text
メール形式: false
見つかったID: 2026-0420
```

---

## 5. ミニ演習（10分）
Step 4の完成コードを基準に、レベル1からレベル3まで順番に進めてください。各レベルは直前の変更を残したまま追記・変更します。

### レベル1（基本）
1. `emailPattern`を変更し、既存の`user@example.com`を簡易チェックする正規表現を作る（厳密すぎなくてよい）。

確認対象の出力（抜粋）:
```text
メール形式: true
```

### レベル2（拡張）
1. レベル1のメール形式チェックを残したまま、`text`を`"A12 B34 C56"`へ変更する。
2. `numberPattern`と検索処理を変更し、`find()`で2桁数字を順に取り出す。

確認対象の出力（抜粋）:
```text
12
34
56
```

### レベル3（実務）
1. レベル2まで完了したコードの検索対象を`"A12"`にする。
2. `Pattern.compile("\\d{2}")` と `Pattern.compile("^\\d{2}$")` で、それぞれ `find()` の結果を確認する。

期待状態:
- アンカーなしでは`12`を部分一致で検出でき、アンカーありでは文字列全体が2桁数字ではないため検出できない

---

## 6. つまずきポイント
- `\d` と書いてコンパイルエラー
  -> Java文字列では `"\\d"` と書く
- `.` をそのまま使って想定外に一致
  -> リテラルのドットは `"\\."`
- `matches()` で部分一致したい
  -> 部分一致は `find()` を使う
