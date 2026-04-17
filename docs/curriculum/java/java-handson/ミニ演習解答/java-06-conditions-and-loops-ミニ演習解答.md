# Java-06 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-06-conditions-and-loops.md`

## レベル1（基本）解答
```java
if (stock <= 0) {
    System.out.println("在庫なし");
} else if (stock < 10) {
    System.out.println("在庫少");
} else if (stock >= 100) {
    System.out.println("在庫十分");
} else {
    System.out.println("在庫あり");
}
```

---

## レベル2（拡張）解答
```java
for (int i = 1; i <= 12; i++) {
    if (i % 2 == 0) {
        System.out.println(i); // 2,4,6,8,10,12
    }
}

int countdown = 3;
while (countdown >= 1) {
    System.out.println(countdown);
    countdown--;
}
```

---

## レベル3（実務）解答
```java
int score = Integer.parseInt(args[0]);

if (score < 0 || score > 100) {
    System.out.println("不正な点数です！");
} else if (score <= 59) {
    System.out.println("赤点です！");
} else if (score <= 79) {
    System.out.println("普通です！");
} else {
    System.out.println("優秀です！");
    if (score == 100) {
        System.out.println("満点だったので宿題免除です！！");
    }
}
```

---

## 実行前予想問題の解答
表示順: `1`, `3`, `4`

---

## デバッグ演習（任意）の解答
`if stock <= 0` は構文エラー。  
`if (stock <= 0)` に戻して再コンパイルする。
