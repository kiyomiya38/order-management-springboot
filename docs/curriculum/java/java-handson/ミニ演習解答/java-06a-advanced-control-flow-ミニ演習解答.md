# Java-06A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-06a-advanced-control-flow.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
`status` を `"PENDING"` に変更する。

```java
String status = "PENDING";
```

期待出力:
```text
状態: 入金待ち
```

次に `status` を `"CANCELLED"` に変更する。

```java
String status = "CANCELLED";
```

期待出力:
```text
状態: 不明
```

---

## レベル2（拡張）解答
レベル1の完成コードを引き継ぎ、`countdown`だけを変更する。
```java
int countdown = 5;
```

Step 4の `do-while` は変更せず、そのまま使用する。

期待出力:
```text
開始まで: 5
開始まで: 4
開始まで: 3
開始まで: 2
開始まで: 1
```

---

## レベル3（実務）解答
レベル2の完成コードを引き継ぎ、不正データ条件だけを変更する。

```java
if (row == 3 && col == 1) {
    System.out.println("不正データ検出: row=" + row + ", col=" + col);
    break inspection;
}
```

期待出力の末尾:
```text
確認済み: row=2, col=2
確認済み: row=2, col=3
不正データ検出: row=3, col=1
```

ラベル付き `break inspection;` により、内側と外側の両方のループが終了する。

---

## 実行前予想問題の解答
`int n = 0; do { n++; } while (n < 0);` の結果は `1`

---

## デバッグ演習（任意）の解答
`case "PAID":` の `break;` を外すと、`PAID` の処理後に `PENDING` の処理まで実行される。

```text
状態: 入金済み
状態: 入金待ち
```

これはフォールスルーと呼ばれる。`case "PAID":` の処理末尾に `break;` を戻すと、`状態: 入金済み` だけが表示される。
