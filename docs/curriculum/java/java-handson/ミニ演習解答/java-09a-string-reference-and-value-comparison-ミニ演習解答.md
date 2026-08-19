# Java-09A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-09a-string-reference-and-value-comparison.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 4の `sameValue` を変更する。

```java
String sameValue = new String("PENDING");
```

期待出力例:
```text
sameValue == original: false
sameValue.equals(original): false
```

---

## レベル2（拡張）解答
レベル1の変数定義へ `anotherReference` を追加する。`sameValue`の変更は残す。

```java
String anotherReference = original;
```

比較結果を表示する。

```java
System.out.println("anotherReference == original: " + (anotherReference == original));
System.out.println("anotherReference.equals(original): " + anotherReference.equals(original));
```

期待出力例:
```text
anotherReference == original: true
anotherReference.equals(original): true
```

---

## レベル3（実務）解答
レベル2の `statuses` を変更し、`pendingCount` を追加する。比較用変数と表示は残す。

```java
String[] statuses = {"PAID", "PENDING", "CANCELLED", "PENDING"};
int paidCount = 0;
int pendingCount = 0;

for (String status : statuses) {
    if ("PAID".equals(status)) {
        paidCount++;
    }
    if ("PENDING".equals(status)) {
        pendingCount++;
    }
}

System.out.println("PAID件数: " + paidCount);
System.out.println("PENDING件数: " + pendingCount);
```

期待出力例:
```text
PAID件数: 1
PENDING件数: 2
```

---

## 実行前予想問題の解答
1. `a == b` は、別々のインスタンスを参照しているため `false`
2. `a.equals(b)` は、文字列の内容が同じため `true`

---

## デバッグ演習（任意）の解答
`sameValue == original` は参照先を比較するため `false`、`sameValue.equals(original)` は文字列の内容を比較するため `true`。
