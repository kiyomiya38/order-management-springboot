# Java-20A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-20a-record-enum.md`

## ミニ演習解答
1. `ApiStatus.ERROR` を追加して表示:

```java
enum ApiStatus {
    OK,
    CREATED,
    ERROR
}

public class RecordEnumDemo {
    public static void main(String[] args) {
        System.out.println(ApiStatus.ERROR);
    }
}
```

2. `Message` に `source` を追加:

```java
enum ApiStatus {
    CREATED
}

record Message(long id, String name, String text, String source) {
}

public class RecordEnumDemo {
    public static void main(String[] args) {
        Message message = new Message(1, "Taro", "こんにちは、Taroさん", "web");
        System.out.println(toMessageJson(message, ApiStatus.CREATED));
    }

    static String toMessageJson(Message message, ApiStatus status) {
        return "{"
                + "\"status\":\"" + status + "\","
                + "\"id\":" + message.id() + ","
                + "\"name\":\"" + message.name() + "\","
                + "\"message\":\"" + message.text() + "\","
                + "\"source\":\"" + message.source() + "\""
                + "}";
    }
}
```

3. `String` と `enum` の違い:
- `String status = "CRETAED";` は打ち間違っても文字列として成立する
- `ApiStatus.CREATEED` は `enum` に存在しない候補なのでコンパイルエラーになる
- 決まった候補だけを使いたい状態値は `enum` にすると安全に扱いやすい
