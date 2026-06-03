# Java-20A 補講: `record` / `enum` 入門

対応参考資料: Lesson0前補講

## 1. この資料のゴール
- `record` を使って、値をまとめるデータ型を定義できる
- `enum` を使って、決まった候補だけを表す型を定義できる
- Lesson0 の `Message` と `ApiStatus` を読める

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- Java-18〜20の内容を実施済み

---

## 3. 先に覚えるポイント
1. `record` は「値をまとめるだけのクラス」を短く書く構文
2. `record Message(long id, String name)` と書くと、`id()` / `name()` という取得メソッドが自動で作られる
3. `record` の値は作成後に変更しない設計になる
4. `enum` は `OK` / `CREATED` のような決まった候補だけを表す型
5. `String` で状態を表すより、`enum` の方が入力ミスを防ぎやすい

### `record` の基本
```java
record Message(long id, String name, String text) {
}
```

ポイント:
- `id`, `name`, `text` をまとめたデータ型を定義している
- 値の取得は `message.id()` のように書く
- `getId()` ではない
- コンストラクタ、`toString`、`equals` などが自動で用意される

### `enum` の基本
```java
enum ApiStatus {
    OK,
    CREATED
}
```

ポイント:
- `ApiStatus` 型の値は `OK` または `CREATED` に限定される
- `"CRETAED"` のような文字列の打ち間違いを避けやすい
- Lesson0ではAPIの結果状態を表すために使う

---

## 4. ハンズオン

目的:
- Lesson0で使う `record` と `enum` を、Web API風の小さいコードで確認する

完了条件:
- `RecordEnumDemo.java` で `record` の値取得と `enum` の候補制限を説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson20a/RecordEnumDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson20a
cd ~/order-management-springboot/practice/java/handson20a
```

### Step 1: `record` でメッセージ1件を表す
`RecordEnumDemo.java` を次の内容で作成:

```java
record Message(long id, String name, String text) { // メッセージ1件分のデータ
}

public class RecordEnumDemo { // record の基本確認クラス
    public static void main(String[] args) {
        Message message = new Message(1, "Taro", "こんにちは、Taroさん"); // record の値を作成

        System.out.println("id=" + message.id()); // id の取得
        System.out.println("name=" + message.name()); // name の取得
        System.out.println("text=" + message.text()); // text の取得
        System.out.println(message); // record は toString も自動生成される
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 RecordEnumDemo.java
java RecordEnumDemo
```

期待出力例:
```text
id=1
name=Taro
text=こんにちは、Taroさん
Message[id=1, name=Taro, text=こんにちは、Taroさん]
```

コード解説:
- `new Message(...)` の引数は、`record Message(...)` の項目順と一致する
- `message.id()` / `message.name()` / `message.text()` で値を取得する
- データを保持するだけなら、通常のクラスより短く書ける

### Step 2: `enum` でAPI状態を表す
`RecordEnumDemo.java` を次の内容に更新:

```java
enum ApiStatus { // APIの結果状態
    OK,
    CREATED
}

record ApiResponse(ApiStatus status, String message) { // APIレスポンス風データ
}

public class RecordEnumDemo { // enum の基本確認クラス
    public static void main(String[] args) {
        ApiResponse health = new ApiResponse(ApiStatus.OK, "ready"); // 起動確認の結果
        ApiResponse created = new ApiResponse(ApiStatus.CREATED, "created"); // 登録成功の結果

        System.out.println(health.status() + ": " + health.message());
        System.out.println(created.status() + ": " + created.message());
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 RecordEnumDemo.java
java RecordEnumDemo
```

期待出力例:
```text
OK: ready
CREATED: created
```

コード解説:
- `ApiStatus.OK` は `ApiStatus` 型の値
- `ApiStatus.CREATED` も `ApiStatus` 型の値
- `ApiStatus.CREATEED` のように存在しない候補を書くとコンパイルエラーになる

### Step 3: Lesson0風のJSON文字列を作る（仕上げ）
`RecordEnumDemo.java` を次の内容に更新:

```java
enum ApiStatus { // APIの結果状態
    CREATED
}

record Message(long id, String name, String text) { // メッセージ1件分のデータ
}

public class RecordEnumDemo { // Lesson0 の Message / ApiStatus 先読みクラス
    public static void main(String[] args) {
        Message message = new Message(1, "Taro", "こんにちは、Taroさん");
        String json = toMessageJson(message, ApiStatus.CREATED);

        System.out.println(json);
    } // main メソッドの終わり

    static String toMessageJson(Message message, ApiStatus status) { // record と enum からJSON文字列を作る
        return "{"
                + "\"status\":\"" + status + "\","
                + "\"id\":" + message.id() + ","
                + "\"name\":\"" + message.name() + "\","
                + "\"message\":\"" + message.text() + "\""
                + "}";
    } // toMessageJson の終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 RecordEnumDemo.java
java RecordEnumDemo
```

期待出力:
```json
{"status":"CREATED","id":1,"name":"Taro","message":"こんにちは、Taroさん"}
```

コード解説:
- `Message` はデータ1件を表す
- `ApiStatus.CREATED` はAPI結果の状態を表す
- Lesson0の `toMessageJson(...)` も同じ考え方で、Javaの値からJSON文字列を作る

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `enum ApiStatus` に `ERROR` を追加し、`ApiStatus.ERROR` を表示する。

期待出力例:
```text
ERROR
```

### レベル2（拡張）
1. `record Message(long id, String name, String text)` に `String source` を追加する。
2. `new Message(...)` と `toMessageJson(...)` も合わせて修正する。

期待出力例:
```json
{"status":"CREATED","id":1,"name":"Taro","message":"こんにちは、Taroさん","source":"web"}
```

### レベル3（実務）
1. `String status = "CRETAED";` のような文字列状態と、`ApiStatus.CREATED` の違いを説明する。

期待結果:
- `enum` は候補外の値をコンパイル時に検出しやすい、と説明できる

---

## 6. つまずきポイント
- `message.getId()` と書いてしまう
  -> `record` の取得メソッドは `message.id()` の形式
- `record` の項目を増やしたのに `new Message(...)` を直していない
  -> recordの定義と生成時の引数を一致させる
- `ApiStatus.CREATED` ではなく `"CREATED"` と混同する
  -> 前者は `ApiStatus` 型、後者は `String` 型
- `enum` の候補名を小文字で書く
  -> 慣例として `OK` / `CREATED` のように大文字で書く
