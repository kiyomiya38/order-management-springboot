# Java-20A 補講: `record` / `enum` 入門

## 1. この資料のゴール
- `record` を使わない場合と使う場合を見比べて、利点を説明できる
- `enum` を使わない場合と使う場合を見比べて、利点を説明できる
- `web-app(簡易版)` Lesson2の `Message` と `ApiStatus` を読める

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
3. `record` は、コンストラクタ、`toString()`、`equals()`、`hashCode()` も自動で用意される
4. `enum` は `OK` / `CREATED` / `ERROR` のような決まった候補だけを表す型
5. `String` で状態を表すより、`enum` の方が入力ミスをコンパイル時に見つけやすい

比較イメージ:

| やりたいこと | 使わないパターン | 使用したパターン |
| --- | --- | --- |
| データをまとめる | 通常の `class` にフィールド、コンストラクタ、取得メソッドを書く | `record` で短く定義する |
| 決まった状態を表す | `String status = "CREATED"` のように文字列で書く | `enum ApiStatus.CREATED` のように候補から選ぶ |

---

## 4. ハンズオン

目的:
- `record` と `enum` の利点を、使わないコードと使ったコードの比較で確認する

完了条件:
- `record` は通常クラスより記述を減らせる、と説明できる
- `enum` は文字列の打ち間違いを防ぎやすい、と説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson20a/RecordEnumDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson20a
cd ~/order-management-springboot/practice/java/handson20a
```

### Step 1: `record` を使わずにメッセージ1件を表す
まずは `record` を使わず、通常の `class` でデータをまとめます。

`RecordEnumDemo.java` を次の内容で作成:

```java
class Message { // record を使わず、通常の class でメッセージ1件分のデータを表す
    private final long id; // id を保存するフィールド。final なのでコンストラクタで設定した後は変更しない
    private final String name; // 名前を保存するフィールド。private なのでクラスの外から直接触れない
    private final String text; // 本文を保存するフィールド

    Message(long id, String name, String text) { // new Message(...) と書いたときに呼ばれるコンストラクタ
        this.id = id; // 引数で受け取った id を、このオブジェクトの id フィールドに保存する
        this.name = name; // 引数で受け取った name を、このオブジェクトの name フィールドに保存する
        this.text = text; // 引数で受け取った text を、このオブジェクトの text フィールドに保存する
    }

    long getId() { // id をクラスの外から読むためのメソッド
        return id;
    }

    String getName() { // name をクラスの外から読むためのメソッド
        return name;
    }

    String getText() { // text をクラスの外から読むためのメソッド
        return text;
    }

    @Override
    public String toString() { // System.out.println(message) で見やすく表示するためのメソッド
        return "Message[id=" + id + ", name=" + name + ", text=" + text + "]";
    }
}

public class RecordEnumDemo { // 通常の class で Message を作る確認用クラス
    public static void main(String[] args) {
        Message message = new Message(1, "Taro", "こんにちは、Taroさん"); // Message 型の値を1件作る

        System.out.println("id=" + message.getId()); // getId() を呼び出して id を取得する
        System.out.println("name=" + message.getName()); // getName() を呼び出して name を取得する
        System.out.println("text=" + message.getText()); // getText() を呼び出して text を取得する
        System.out.println(message); // toString() の結果が表示される
    } // main メソッドの処理はここまで
} // RecordEnumDemo クラスの定義はここまで
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

確認ポイント:
- データを3つ持つだけでも、フィールド、コンストラクタ、取得メソッド、`toString()` が必要になる
- `toString()` を自分で書かないと、`System.out.println(message)` の表示が分かりにくくなる

### Step 2: `record` を使って同じメッセージ1件を表す
Step 1 と同じ目的のコードを、`record` で短く書きます。

`RecordEnumDemo.java` を次の内容に更新:

```java
record Message(long id, String name, String text) { // id、name、text をまとめるデータ型。取得メソッドや toString() は自動で作られる
}

public class RecordEnumDemo { // record で Message を作る確認用クラス
    public static void main(String[] args) {
        Message message = new Message(1, "Taro", "こんにちは、Taroさん"); // record も new で値を作る

        System.out.println("id=" + message.id()); // record の取得メソッドは getId() ではなく id()
        System.out.println("name=" + message.name()); // name の値を取得する
        System.out.println("text=" + message.text()); // text の値を取得する
        System.out.println(message); // record は toString() が自動で用意される
    } // main メソッドの処理はここまで
} // RecordEnumDemo クラスの定義はここまで
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

比較ポイント:
- Step 1 と同じ出力だが、`record` ではコード量が大きく減る
- `id()` / `name()` / `text()` は、`record Message(...)` の項目から自動で作られる
- データを保持するだけの型なら、通常の `class` より `record` が読みやすい

### Step 3: `enum` を使わずにAPI状態を文字列で表す
次は `enum` を使わず、APIの状態を `String` で表します。

`RecordEnumDemo.java` を次の内容に更新:

```java
public class RecordEnumDemo { // String で API 状態を表す確認用クラス
    public static void main(String[] args) {
        String status = "CRETAED"; // "CREATED" のつもりだが、スペルミスしている。String なのでコンパイルは通る
        String message = "created"; // API レスポンスのメッセージ

        System.out.println("status=" + status); // スペルミスした文字列もそのまま表示される

        if ("CREATED".equals(status)) { // status が正しい "CREATED" と一致するか確認する
            System.out.println("登録成功として処理します");
        } else {
            System.out.println("登録成功として扱えません"); // スペルミスにより、期待した分岐に入らない
        }

        String json = "{\"status\":\"" + status + "\",\"message\":\"" + message + "\"}"; // 文字列をつなげて JSON 風の文字列を作る
        System.out.println(json); // status のスペルミスも JSON に入ってしまう
    } // main メソッドの処理はここまで
} // RecordEnumDemo クラスの定義はここまで
```

実行:
```bash
javac -encoding UTF-8 RecordEnumDemo.java
java RecordEnumDemo
```

期待出力例:
```text
status=CRETAED
登録成功として扱えません
{"status":"CRETAED","message":"created"}
```

確認ポイント:
- `"CRETAED"` はスペルミスだが、Java から見るとただの文字列なのでコンパイルエラーにならない
- 文字列で状態を表すと、実行して初めてミスに気づくことがある

### Step 4: `enum` を使ってAPI状態を候補から選ぶ
Step 3 と同じAPI状態を、`enum` で表します。

`RecordEnumDemo.java` を次の内容に更新:

```java
enum ApiStatus { // API 状態として使える候補をここで固定する
    OK,
    CREATED,
    ERROR
}

record ApiResponse(ApiStatus status, String message) { // status は自由な文字列ではなく ApiStatus 型にする
}

public class RecordEnumDemo { // enum で API 状態を表す確認用クラス
    public static void main(String[] args) {
        ApiResponse response = new ApiResponse(ApiStatus.CREATED, "created"); // CREATED は ApiStatus に定義済みの候補
        // ApiStatus.CRETAED と書くと、候補に存在しないためコンパイルエラーになる

        System.out.println("status=" + response.status()); // enum の値は CREATED のような名前で表示される

        if (response.status() == ApiStatus.CREATED) { // enum は決まった候補同士なので == で比較できる
            System.out.println("登録成功として処理します");
        } else {
            System.out.println("登録成功として扱えません");
        }

        String json = "{\"status\":\"" + response.status() + "\",\"message\":\"" + response.message() + "\"}"; // enum の値を JSON 風の文字列に入れる
        System.out.println(json);
    } // main メソッドの処理はここまで
} // RecordEnumDemo クラスの定義はここまで
```

実行:
```bash
javac -encoding UTF-8 RecordEnumDemo.java
java RecordEnumDemo
```

期待出力例:
```text
status=CREATED
登録成功として処理します
{"status":"CREATED","message":"created"}
```

比較ポイント:
- `ApiStatus.CREATED` は、`ApiStatus` に定義された候補から選んでいる
- 存在しない `ApiStatus.CRETAED` はコンパイルエラーになるため、実行前にミスを見つけやすい
- 状態の種類が決まっている場合は、`String` より `enum` の方が安全

### Step 5: `record` + `enum` でWeb API風レスポンスを作る（仕上げ）
最後に、`record` と `enum` を組み合わせて `web-app(簡易版)` Lesson2 に近い形にします。

`RecordEnumDemo.java` を次の内容に更新:

```java
enum ApiStatus { // API レスポンスで使う状態を候補として定義する
    CREATED,
    ERROR
}

record Message(long id, String name, String text) { // メッセージ1件分のデータをまとめる record
}

record ApiResponse(ApiStatus status, Message message) { // API 状態とメッセージ本体をまとめる record
}

public class RecordEnumDemo { // Web アプリ Lesson2 の Message / ApiStatus 先読みクラス
    public static void main(String[] args) {
        Message message = new Message(1, "Taro", "こんにちは、Taroさん"); // 返却したいメッセージデータを作る
        ApiResponse response = new ApiResponse(ApiStatus.CREATED, message); // 状態とデータを1つのレスポンスとしてまとめる

        String json = toMessageJson(response); // record と enum の値から JSON 風の文字列を作る
        System.out.println(json); // 作成した JSON 風の文字列を表示する
    } // main メソッドの処理はここまで

    static String toMessageJson(ApiResponse response) { // API レスポンスを JSON 風の文字列に変換するメソッド
        Message message = response.message(); // response の中に入っている Message を取り出す

        return "{"
                + "\"status\":\"" + response.status() + "\"," // enum の CREATED を文字列として入れる
                + "\"id\":" + message.id() + "," // record の id() で id を取り出して入れる
                + "\"name\":\"" + message.name() + "\"," // record の name() で名前を取り出して入れる
                + "\"message\":\"" + message.text() + "\"" // record の text() で本文を取り出して入れる
                + "}";
    } // toMessageJson メソッドの処理はここまで
} // RecordEnumDemo クラスの定義はここまで
```

実行:
```bash
javac -encoding UTF-8 RecordEnumDemo.java
java RecordEnumDemo
```

期待出力例:
```json
{"status":"CREATED","id":1,"name":"Taro","message":"こんにちは、Taroさん"}
```

コード解説:
- `Message` は、APIで返すメッセージ1件分のデータを表す
- `ApiStatus.CREATED` は、API結果の状態を決まった候補から選んでいる
- `ApiResponse` は、状態とデータ本体をまとめて扱うための型
- `web-app(簡易版)` Lesson2の `Message` と `ApiStatus` も同じ考え方で使う

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. Step 2 の `record Message(long id, String name, String text)` に `String source` を追加する。
2. `new Message(...)` と表示処理も合わせて修正する。

期待出力例:
```text
source=web
```

### レベル2（拡張）
1. Step 4 の `enum ApiStatus` に `DELETED` を追加する。
2. `ApiStatus.DELETED` を使ってレスポンスを作り、表示する。

期待出力例:
```text
status=DELETED
```

### レベル3（実務）
1. Step 3 の `String status = "CRETAED";` と、Step 4 の `ApiStatus.CREATED` の違いを説明する。

期待状態:
- `String` は存在しない状態名でもコンパイルが通る、と説明できる
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
  -> 慣例として `OK` / `CREATED` / `ERROR` のように大文字で書く
- `ApiStatus.CRETAED` のように候補にない名前を書く
  -> enum は候補にない値を使えないため、コンパイルエラーになる
