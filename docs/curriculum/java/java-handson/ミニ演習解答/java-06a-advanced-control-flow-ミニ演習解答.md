# Java-06A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-06a-advanced-control-flow.md`

## レベル1（基本）解答
```java
String status = "PAID";
switch (status) {
    case "PAID":
        System.out.println("入金済み");
        break;
    case "PENDING":
        System.out.println("未入金");
        break;
    default:
        System.out.println("不明な状態");
        break;
}
```

---

## レベル2（拡張）解答
```java
int n = 3;
do {
    System.out.println(n);
    n--;
} while (n >= 1);
```

---

## レベル3（実務）解答
```java
int[][] rows = {
        {10, 20, 30},
        {40, -1, 50},
        {60, 70, 80}
};

outer:
for (int row = 0; row < rows.length; row++) {
    for (int col = 0; col < rows[row].length; col++) {
        if (rows[row][col] < 0) {
            System.out.println("不正データ検出: row=" + row + ", col=" + col);
            break outer;
        }
    }
}
```

---

## 実行前予想問題の解答
`int n = 0; do { n++; } while (n < 0);` の結果は `1`

---

## デバッグ演習（任意）の解答
`switch` の `break` を外すとフォールスルーで次ケースまで実行される。  
意図しない出力を確認したら `break` を戻す。
