# Java-11A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-11a-constructor-chaining.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答

`User`へ引数なしコンストラクタを追加する。

```java
User() {
    this("guest");
}
```

`main(...)`の既存処理より後へ、生成と表示を追加する。

```java
User guest = new User();
System.out.println(guest.name);
```

`guest`の名前は`guest`になる。

---

## レベル2（拡張）解答
レベル1の引数なしコンストラクタを残し、次を追加・変更する。

```java
String role;

User(String name) {
    this(name, "member");
}

User(String name, String role) {
    this.name = name;
    this.role = role;
}
```

`main(...)`へ生成と表示を追加する。

```java
User member = new User("Tanaka");
System.out.println(member.name + " / " + member.role);
```

期待出力は`Tanaka / member`。

---

## レベル3（実務）解答
レベル2の完成コードで、エラー確認用の変更だけを一時的に行う。
`this(...)` より前へ処理を書くとコンパイルエラーになる。

```java
User(String name) {
    this.name = name;
    this(name, "member");
}
```

エラーを確認したら、確認用に追加した`this.name = name;`だけを削除し、次の正常なコードへ戻す。

```java
User(String name) {
    this(name, "member");
}
```

`javac -encoding UTF-8 ConstructorChainingDemo.java`を再実行し、コンパイルに成功することを確認する。`this(...)`はコンストラクタの最初の文にする。
