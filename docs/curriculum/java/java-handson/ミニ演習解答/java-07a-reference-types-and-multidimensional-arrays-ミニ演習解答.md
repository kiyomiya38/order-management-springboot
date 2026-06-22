# Java-07A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-07a-reference-types-and-multidimensional-arrays.md`

## レベル1（基本）解答
```java
int[] a = {1, 2, 3};
int[] b = a;          // 参照コピー
int[] c = {1, 2, 3};  // 別配列

b[0] = 99;
System.out.println(a[0]); // 99
System.out.println(c[0]); // 1
```

---

## レベル2（拡張）解答
```java
int[][] seats = {
        {101, 102, 103},
        {201, 202},
        {301, 302, 303, 304}
};

for (int row = 0; row < seats.length; row++) {
    for (int col = 0; col < seats[row].length; col++) {
        System.out.println(seats[row][col]);
    }
}
```

---

## レベル3（実務）解答
```java
int[][] seats = {{101, 102, 103}, {201, 202, 203}, {301, 302, 303}};
int total = 0;

for (int row = 0; row < seats.length; row++) {
    int rowTotal = 0;
    for (int col = 0; col < seats[row].length; col++) {
        rowTotal += seats[row][col];
    }
    total += rowTotal;
    System.out.println("row " + row + " 合計: " + rowTotal);
}
System.out.println("全体合計: " + total);
```

---

## 実行前予想問題の解答
1. `values.length` は行数なので `2`
2. `values[1].length` は2行目の列数なので `1`

---

## デバッグ演習（任意）の解答
`col <= seats[row].length` は末尾+1を参照して `ArrayIndexOutOfBoundsException`。  
`col < seats[row].length` に戻す。
