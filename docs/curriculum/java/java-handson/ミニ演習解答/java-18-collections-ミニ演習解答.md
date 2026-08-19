# Java-18 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-18-collections.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3のimportへ`ArrayList`と`List`を追記し、既存の`main`内にある`Map`処理より後へ次の処理を追加する。既存の`Map`処理は残す:

```java
import java.util.ArrayList;
import java.util.List;
```

```java
// レベル1で追記
List<String> products = new ArrayList<>();
products.add("Keyboard");
products.add("Mouse");
products.add("Monitor");
products.add("Display");
products.add("Dock");

// 拡張forでproductsから商品名を1件ずつ取り出す
for (String product : products) {
    System.out.println(product);
}
```

## レベル2（拡張）解答
レベル1の`List`と既存の`Map`を残し、importと`List`処理より後へ`Set`処理を追記する:

```java
import java.util.HashSet;
import java.util.Set;
```

```java
// レベル2で追記
Set<String> tags = new HashSet<>();
tags.add("PAID");
tags.add("PAID");
tags.add("URGENT");
System.out.println(tags.size()); // 2
System.out.println(tags); // 順序は保証されない
```

## レベル3（実務）解答
レベル1・2の処理を残し、Step 3にある既存の`stockByCode`初期化処理へ上書き行を追記する。`Map`を作り直す必要はない:

```java
stockByCode.put("P-001", 12);
stockByCode.put("P-001", 20); // 上書き
System.out.println("P-001 -> " + stockByCode.get("P-001"));
```
