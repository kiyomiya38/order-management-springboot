# Java-20A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-20a-record-enum.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 5の`Message`へ`source`を追加し、演習で指定された`"web"`を渡すように生成処理と`toMessageJson(...)`を変更する。他のrecordとenumは残す。

```java
record Message(long id, String name, String text, String source) {
}
```

```java
Message message = new Message(1, "Taro", "こんにちは、Taroさん", "web");
```

`toMessageJson(...)`は、レベル1で追加した`source`を含む次の内容へ変更する:

```java
static String toMessageJson(ApiResponse response) {
    Message message = response.message();

    return "{"
            + "\"status\":\"" + response.status() + "\","
            + "\"id\":" + message.id() + ","
            + "\"name\":\"" + message.name() + "\","
            + "\"message\":\"" + message.text() + "\","
            + "\"source\":\"" + message.source() + "\""
            + "}";
}
```

## レベル2（拡張）解答
レベル1で追加した`source`を残したまま、Step 5のenumへ`DELETED`を追加する。

```java
enum ApiStatus {
    CREATED,
    ERROR,
    DELETED
}
```

`main`の`ApiResponse`生成処理を変更する:

```java
ApiResponse response = new ApiResponse(ApiStatus.DELETED, message);
System.out.println(toMessageJson(response));
```

## レベル3（実務）解答
レベル1・2のコードはそのまま残し、Step 3と比較して次のように説明する:

- `String status = "CRETAED";` は打ち間違っても文字列として成立する
- `ApiStatus.CRETAED` は `enum` に存在しない候補なのでコンパイルエラーになる
- 決まった候補だけを使いたい状態値は `enum` にすると安全に扱いやすい
