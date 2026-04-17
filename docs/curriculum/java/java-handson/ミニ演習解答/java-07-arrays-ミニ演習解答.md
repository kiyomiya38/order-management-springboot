# Java-07 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-07-arrays.md`

## レベル1（基本）解答
```java
String[] productNames = {"Laptop", "Mouse", "Keyboard", "Display"};
int[] quantities = {3, 5, 2, 4};

for (int i = 0; i < productNames.length; i++) {
    System.out.println(productNames[i] + " 数量: " + quantities[i]);
}
```

---

## レベル2（拡張）解答
```java
int max = quantities[0];
for (int q : quantities) {
    if (q > max) {
        max = q;
    }
}
System.out.println("最大数量: " + max);
```

---

## レベル3（実務）解答
```java
int total = 0;
for (int q : quantities) { // 単純走査は for-each
    total += q;
}

for (int i = 0; i < productNames.length; i++) { // インデックス利用時は通常 for
    System.out.println(i + ":" + productNames[i]);
}
```

---

## 実行前予想問題の解答
1. `nums.length` -> `4`  
2. `nums[nums.length - 1]` -> `8`

---

## デバッグ演習（任意）の解答
`i <= quantities.length` は最後に `quantities[4]` を参照して  
`ArrayIndexOutOfBoundsException` になる。`i < quantities.length` に戻す。
