# Java-09 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-09-instances-and-classes.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3に `c3` の生成、初期化、表示を追加する。

```java
Customer c3 = new Customer();
c3.name = "Sato";
c3.point = 50;

System.out.println(c3.name + " point: " + c3.point);
```

期待出力:
```text
Tanaka point: 150
Suzuki point: 100
Sato point: 50
```

---

## レベル2（拡張）解答
レベル1の完成コードで、`c1`の加算値だけを変更する。`c3`は残す。
`c1` の加算値だけを変更する。

```java
c1.addPoint(50);
```

期待出力:
```text
Tanaka point: 170
Suzuki point: 100
```

---

## レベル3（実務）解答
レベル2の完成コードを引き継いで実施する。
`Customer` クラスに次のメソッドを追加する。

```java
void usePoint(int value) {
    point -= value;
    if (point < 0) {
        point = 0;
    }
}
```

Step 3の加算処理後、既存の表示処理より前に次の呼び出しを追加する。

```java
c1.usePoint(200);
```

その後の `c1` と `c2` の表示処理は変更せずに使用する。

期待出力:
```text
Tanaka point: 0
Suzuki point: 100
Sato point: 50
```

---

## 実行前予想問題の解答
`c1` と `c2` は別のインスタンスなので、それぞれ独立して更新される。

```text
Tanaka point: 130
Suzuki point: 130
```

---

## デバッグ演習（任意）の解答
`Customer` クラスには `addPoints(...)` というメソッドがないため、`cannot find symbol` が表示される。

```text
symbol: method addPoints(int)
```

呼び出すメソッド名を、定義されている `addPoint(...)` に戻す。

```java
c1.addPoint(30);
```
