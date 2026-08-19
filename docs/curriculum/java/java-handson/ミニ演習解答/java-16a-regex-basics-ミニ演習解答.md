# Java-16A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-16a-regex-basics.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 4の`emailPattern`を次のように変更する:

```java
// compile(...)で、簡易メール形式を確認する検索ルールを作る
Pattern emailPattern = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
```

既存の表示処理により`メール形式: true`と表示される。

## レベル2（拡張）解答
レベル1のメール形式チェックを残したまま、`text`、`numberPattern`、検索処理を次の内容へ変更する:

```java
String text = "A12 B34 C56";
Pattern numberPattern = Pattern.compile("\\d{2}"); // 2桁の数字という検索ルール
Matcher numberMatcher = numberPattern.matcher(text); // textを検索対象に設定
while (numberMatcher.find()) { // 次に一致する部分がある間は繰り返す
    System.out.println(numberMatcher.group()); // 見つかった12、34、56を順に取得して表示
}
```

## レベル3（実務）解答
レベル1のメール形式チェックを残し、レベル2の数値検索部分を次の確認処理へ置き換える:

```java
String text = "A12";
System.out.println("アンカーなし: "
        + Pattern.compile("\\d{2}") // 2桁の数字を探すルール
                .matcher(text)     // textを検索対象にする
                .find());          // 部分一致の12が見つかるためtrue
System.out.println("アンカーあり: "
        + Pattern.compile("^\\d{2}$") // 文字列全体が2桁数字というルール
                .matcher(text)       // textを検索対象にする
                .find());            // A12全体は2桁数字ではないためfalse
```

アンカーなしは部分一致でき、`^`と`$`を付けると文字列全体が2桁数字の場合だけ一致する。
