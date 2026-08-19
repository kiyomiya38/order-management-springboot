# Java-07 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-07-arrays.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
```java
String[] productNames = {"Laptop", "Mouse", "Keyboard", "Monitor"};
int[] quantities = {3, 5, 2, 1};

for (int i = 0; i < productNames.length; i++) {
    System.out.println(productNames[i] + " 数量: " + quantities[i]);
}
```

期待出力:
```text
Laptop 数量: 3
Mouse 数量: 5
Keyboard 数量: 2
Monitor 数量: 1
```

---

## レベル2（拡張）解答
レベル1の完成コードへ、次の最大値計算を追加する。
```java
int max = quantities[0];
for (int i = 1; i < quantities.length; i++) {
    if (quantities[i] > max) {
        max = quantities[i];
    }
}
System.out.println("最大数量: " + max);
```

期待出力:
```text
最大数量: 5
```

---

## レベル3（実務）解答
レベル2の完成コードを引き継ぎ、合計処理とインデックス表示を追加する。
```java
int total = 0;
for (int quantity : quantities) { // インデックスが不要なため拡張forを使う
    total += quantity;
}
System.out.println("合計(拡張for): " + total);

for (int i = 0; i < productNames.length; i++) { // インデックス利用時は通常 for
    System.out.println(i + ": " + productNames[i]);
}
```

期待出力:
```text
合計(拡張for): 11
0: Laptop
1: Mouse
2: Keyboard
3: Monitor
```

---

## 実行前予想問題の解答
1. `nums.length` -> `4`  
2. `nums[nums.length - 1]` -> `8`

---

## デバッグ演習（任意）の解答
`i <= productNames.length` にすると、最後の周回で `i` が `3` になる。
Step 3の `productNames` と `quantities` の有効なインデックスは `0`〜`2` のため、`productNames[3]` を参照した時点で `ArrayIndexOutOfBoundsException` になる。

ループ条件を次のように戻す。

```java
i < productNames.length
```
