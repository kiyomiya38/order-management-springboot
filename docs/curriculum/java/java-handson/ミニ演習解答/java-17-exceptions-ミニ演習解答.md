# Java-17 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-17-exceptions.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3の`validateQuantity`の条件を変更する。このレベルでは既存のメッセージを残してよい:

```java
static int validateQuantity(int quantity) {
    if (quantity <= 0 || quantity > 1000) {
        throw new IllegalArgumentException("quantity は 1 以上 1000 以下である必要があります");
    }
    return quantity;
}
```

`main`の呼び出し値を一時的に`1001`へ変更すると、上限超過を確認できる。確認後は、レベル2の価格検証まで処理が進むように正常値`1`へ変更する:

```java
int q = validateQuantity(1); // 上限超過の確認後に正常値へ変更
```

## レベル2（拡張）解答
レベル1の`validateQuantity`を残したまま、次のメソッドを追記する:

```java
static int validatePrice(int price) {
    if (price < 0) {
        throw new IllegalArgumentException("price は 0 以上である必要があります");
    }
    return price;
}
```

`main`の`try-catch`を次の内容にする。数量はレベル1で戻した正常値`1`を使うため、価格検証まで処理が進む:

```java
try {
    int q = validateQuantity(1);
    System.out.println("数量: " + q);

    int price = validatePrice(-1);
    System.out.println("価格: " + price); // 価格が不正なため実行されない
} catch (IllegalArgumentException e) {
    System.out.println("入力エラー: " + e.getMessage());
}
```

## レベル3（実務）解答
レベル1・2の両メソッドについて、`throw`する行を次のように変更する:

```java
// validateQuantity内
throw new IllegalArgumentException("quantity が不正です: " + quantity);

// validatePrice内
throw new IllegalArgumentException("price が不正です: " + price);
```

両方のメッセージを1回の実行で確認する場合は、`main`で検証ごとに`try` / `catch`を分ける:

```java
public static void main(String[] args) {
    try {
        validateQuantity(1001);
    } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
    }

    try {
        validatePrice(-1);
    } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
    }
}
```
