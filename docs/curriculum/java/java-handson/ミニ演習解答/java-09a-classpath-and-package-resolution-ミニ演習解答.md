# Java-09A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-09a-classpath-and-package-resolution.md`

## レベル1（基本）解答
`CpApp` のメッセージ変更例:

```java
System.out.println("Hello from package app (updated)");
```

---

## レベル2（拡張）解答
`src/util/Printer.java`:

```java
package util;

public class Printer {
    public static void print(String message) {
        System.out.println(message);
    }
}
```

`src/app/CpApp.java`:

```java
package app;

import util.Printer;

public class CpApp {
    public static void main(String[] args) {
        Printer.print("CpApp with util.Printer");
    }
}
```

---

## レベル3（実務）解答
- `java -cp out app.CpApp` は成功  
- `java -cp . app.CpApp` は失敗（`app/CpApp.class` の起点が `.` 側に存在しないため）

---

## 実行前予想問題の解答
成功するのは:
- `java -cp out app.CpApp`

---

## デバッグ演習（任意）の解答
`package app;` を `package apps;` に変更すると、  
`java -cp out app.CpApp` と不一致になり実行失敗。  
`package`・フォルダ・実行FQCNを揃えると復旧する。
