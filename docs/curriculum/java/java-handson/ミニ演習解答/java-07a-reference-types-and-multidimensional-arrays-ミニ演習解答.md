# Java-07A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-07a-reference-types-and-multidimensional-arrays.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3の配列定義と更新処理を次のように変更する。

```java
int[] quantitiesA = {3, 5, 2};
int[] quantitiesB = quantitiesA;       // 参照コピー
int[] quantitiesC = {3, 5, 2};         // 別配列

quantitiesB[0] = 99;
quantitiesB[1] = 88;

String quantitiesAText = Arrays.toString(quantitiesA); // 配列Aを表示用文字列へ変換
String quantitiesBText = Arrays.toString(quantitiesB); // 配列Bを表示用文字列へ変換
String quantitiesCText = Arrays.toString(quantitiesC); // 配列Cを表示用文字列へ変換

System.out.println("A: " + quantitiesAText);
System.out.println("B: " + quantitiesBText);
System.out.println("C: " + quantitiesCText);
```

期待出力:
```text
A: [99, 88, 2]
B: [99, 88, 2]
C: [3, 5, 2]
```

---

## レベル2（拡張）解答
レベル1の完成コードを引き継ぎ、`seats`だけを変更する。
```java
int[][] seats = {
        {101, 102, 103},
        {201, 202},
        {301, 302, 303, 304}
};

for (int row = 0; row < seats.length; row++) { // seats.lengthは行数
    for (int col = 0; col < seats[row].length; col++) { // 現在の行の要素数まで繰り返す
        System.out.println("row=" + row + ", col=" + col + ", seatNo=" + seats[row][col]);
    }
}
```

`seats[row]`は、現在処理している1行分の`int[]`を表す。例えば`row`が`1`なら、`seats[row]`は`{201, 202}`、`seats[row].length`は`2`になる。行を移動するたびに現在の行の要素数を調べるため、列数が異なっても安全に走査できる。

---

## レベル3（実務）解答
レベル2の完成コードへ、次の集計処理を追加する。
```java
int total = 0;

for (int row = 0; row < seats.length; row++) { // 全行を順番に処理
    int rowTotal = 0;
    for (int col = 0; col < seats[row].length; col++) { // 現在の行にある要素だけを処理
        rowTotal += seats[row][col];
    }
    total += rowTotal;
    System.out.println("row " + row + " 合計: " + rowTotal);
}
System.out.println("全体合計: " + total);
```

期待出力:
```text
row 0 合計: 306
row 1 合計: 403
row 2 合計: 1210
全体合計: 1919
```

---

## 実行前予想問題の解答
1. `values.length` は行数なので `2`
2. `values[1].length` は2行目の列数なので `1`

---

## デバッグ演習（任意）の解答
`col <= seats[row].length` にすると、`col` がその行の要素数と等しい場合にもループが実行される。
Step 3の各行は3要素で、有効な列インデックスは `0`〜`2` のため、`seats[0][3]` を参照した時点で `ArrayIndexOutOfBoundsException` になる。

条件を次のように戻す。

```java
col < seats[row].length
```
