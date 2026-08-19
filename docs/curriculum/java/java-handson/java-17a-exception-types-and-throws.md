# Java-17A 補講: 例外の分類と `throws`（checked / unchecked）

## 1. この資料のゴール
- checked例外とunchecked例外の違いを説明できる
- `throws` が必要な場面を判断できる
- `RuntimeException` 系の扱い方を説明できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- 例: `17.0.x`

---

## 3. 先に覚えるポイント
1. checked例外は「コンパイル時に処理強制される例外」
2. unchecked例外は `RuntimeException` 系で、`throws` や `catch` が必須ではない
3. `throws` は「呼び出し側で処理してください」という宣言

### `throw`・`throws`・`catch`の役割

名前が似ていますが、使う場所が異なります。

| 書き方 | 役割 | 書く場所 |
| --- | --- | --- |
| `throw new ...` | その場所で例外を発生させる | メソッドの処理内 |
| `throws ...` | このメソッドから例外が出る可能性を呼び出し側へ伝える | メソッド宣言 |
| `catch (...)` | 発生・伝播してきた例外を受け取り、対処する | `try`の後 |

### checked例外とunchecked例外の違い

どちらも実行時に発生する例外ですが、コンパイラが処理を強制するかが異なります。

#### 分類を決めるのは例外クラスの継承関係

例外がcheckedかuncheckedかは、`catch`や`throws`を書いたかどうかではなく、その例外クラスが`RuntimeException`を継承しているかで決まります。

```text
Throwable
└─ Exception
   ├─ ReflectiveOperationException
   │  └─ ClassNotFoundException          ← checked例外
   │
   └─ RuntimeException
      └─ IllegalArgumentException
         └─ NumberFormatException        ← unchecked例外
```

- `Exception`の子孫で、`RuntimeException`の子孫ではない例外はchecked例外
- `RuntimeException`の子孫はunchecked例外
- 1つの例外クラスが、実行する場面によってcheckedとuncheckedに切り替わることはない

そのため、checked例外とunchecked例外を比較するには、異なる例外クラスを使用する必要があります。

| 種類 | `catch`も`throws`も書かなかった場合 | このハンズオンの例 |
| --- | --- | --- |
| checked例外 | コンパイルエラーになる | `ClassNotFoundException` |
| unchecked例外 | コンパイルできるが、実行時にプログラムが停止する可能性がある | `NumberFormatException` |

`catch`はどちらの例外にも使用できます。コードに`catch`があるかどうかで分類するのではなく、「処理を書かなかったときにコンパイラがエラーにするか」で見分けます。

#### このハンズオンで2種類の例外を選んだ理由

| 使用する例外 | 発生させるコード | 選んだ理由 |
| --- | --- | --- |
| `ClassNotFoundException` | `Class.forName(...)` | 短いコードでchecked例外のコンパイルエラーを確認できる |
| `NumberFormatException` | `Integer.parseInt(...)` | 文字列から整数への変換という身近な処理でunchecked例外を確認できる |

これらはそれぞれの分類を確認するための代表例です。checked例外が常に`ClassNotFoundException`になるわけではなく、unchecked例外が常に`NumberFormatException`になるわけでもありません。このハンズオンで学ぶ中心は各APIの使い方ではなく、例外クラスの分類によってコンパイラの扱いが変わることです。

`throws`で上位へ伝える流れは次のようになります。

```text
loadDriver()内のClass.forName(...)で例外発生
  ↓ loadDriver()のthrows ClassNotFoundException
loadDriver()を呼び出したmain()へ伝わる
  ↓
main()のcatchで受け取る
```

「上位」とは、継承の親クラスではなく、そのメソッドを呼び出した側を意味します。

---

## 4. ハンズオン

目的:
- 例外分類に応じて `catch` / `throws` を使い分ける

完了条件:
- `ExceptionTypesDemo.java` で checked と unchecked の違いを説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson17a/ExceptionTypesDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson17a
cd ~/order-management-springboot/practice/java/handson17a
```

### Step 1: checked例外は処理しないとコンパイルできないことを確認する

ここで`ClassNotFoundException`の詳しい使い方を覚える必要はありません。`RuntimeException`を継承していないchecked例外を未処理にすると、コンパイルできないことが確認の目的です。

最初に、`catch`も`throws`も書いていない未完成コードをコンパイルします。このコードは、checked例外に必要な処理がないため意図的にコンパイルエラーになります。

#### Step 1-1: 未処理のchecked例外を確認する

`ExceptionTypesDemo.java`を次の内容で作成:

```java
public class ExceptionTypesDemo {
    static void loadDriver() {
        // Class.forName(...)は、文字列で指定した名前のクラスをJVMに探して読み込ませる
        // ClassNotFoundExceptionはRuntimeExceptionを継承していないためchecked例外
        // 指定したクラスが見つからない場合に発生する可能性がある
        // 現在はcatchもthrowsも書いていないため、このコードはコンパイルできない
        Class.forName("com.example.NotExistingDriver");
    }

    public static void main(String[] args) {
        loadDriver();
    }
}
```

コンパイル:

```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
```

期待結果:

```text
エラー: 例外ClassNotFoundExceptionは報告されません。スローするには、捕捉または宣言する必要があります
```

エラーメッセージの細部は環境により異なります。ここでは、`ClassNotFoundException`を`catch`するか、`throws`で宣言するよう求められていることを確認します。

#### Step 1-2: `throws`と`catch`を追加してコンパイルできるようにする

今回は`loadDriver()`では対処せず、`throws`で呼び出し側の`main()`へ伝え、`main()`の`catch`で対処します。`ExceptionTypesDemo.java`を次の全コードへ置き換えます。

```java
public class ExceptionTypesDemo {
    // ClassNotFoundException（クラス・ノット・ファウンド・エクセプション）は、
    // RuntimeExceptionを継承していないためchecked例外
    // 指定した名前のクラスが見つからないときに発生する
    static void loadDriver() throws ClassNotFoundException {
        // throwsは、このメソッド内では例外を処理せず、
        // 呼び出し側のmainメソッドに処理を任せるという宣言

        // Class.forName(...)は、文字列で指定した名前のクラスを
        // JVMに探して読み込ませるメソッド
        // 今回は存在しないクラス名を指定し、意図的に例外を発生させる
        Class.forName("com.example.NotExistingDriver");
    }

    public static void main(String[] args) {
        try {
            loadDriver(); // ClassNotFoundExceptionが発生する可能性があるメソッドを呼び出す
        } catch (ClassNotFoundException e) { // 発生した例外を変数eで受け取る
            // e.getClass()は、発生した例外が何のクラスかという情報を取得する
            // getSimpleName()は、取得したクラス情報から短いクラス名だけを取り出す
            // 今回取得できる文字列は「ClassNotFoundException」
            System.out.println("checked例外を捕捉: " + e.getClass().getSimpleName());
        }
    }
}
```

コンパイルして実行:

```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
java ExceptionTypesDemo
```

期待出力例:
```text
checked例外を捕捉: ClassNotFoundException
```

Step 1-1ではコンパイルできませんでしたが、Step 1-2では`throws`と`catch`を追加したためコンパイルできます。これが「checked例外は処理を強制される」という意味です。

### Step 2: unchecked例外は処理しなくてもコンパイルできることを確認する

ここで`NumberFormatException`の詳しい使い方を覚えることだけが目的ではありません。`RuntimeException`を継承するunchecked例外は、未処理でもコンパイルできることが確認の目的です。

次は、`catch`も`throws`も書かずに`NumberFormatException`を発生させます。コンパイルには成功しますが、実行時にプログラムが停止します。

#### Step 2-1: 未処理のunchecked例外を確認する

`ExceptionTypesDemo.java`を次の全コードへ置き換えます。

```java
public class ExceptionTypesDemo {
    // NumberFormatException（ナンバー・フォーマット・エクセプション）は、
    // RuntimeException系の例外なのでunchecked例外
    // 数値に変換できない文字列を数値へ変換したときに発生する
    static int parseQuantity(String raw) {
        // unchecked例外なので、catchもthrowsもない状態でもコンパイルできる
        return Integer.parseInt(raw);
    }

    public static void main(String[] args) {
        // 「abc」は整数に変換できないため、実行時にNumberFormatExceptionが発生する
        int quantity = parseQuantity("abc");

        // 例外でプログラムが停止するため、この行は実行されない
        System.out.println("quantity=" + quantity);
    }
}
```

まずコンパイルします。

```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
```

期待結果:

- コンパイルエラーは表示されない
- `ExceptionTypesDemo.class`が作成される

次に実行します。

```bash
java ExceptionTypesDemo
```

期待結果の一部:

```text
Exception in thread "main" java.lang.NumberFormatException
```

行番号などは環境により異なります。この赤いエラー表示は今回の確認対象です。unchecked例外は未処理でもコンパイルできますが、発生すればプログラムが停止することを確認します。

#### Step 2-2: 必要と判断して`catch`を追加する

利用者が入力した値を扱う処理では、入力間違いでプログラムを終了させず、分かりやすいメッセージを返したい場合があります。今回はそのように判断し、`catch`を追加します。`ExceptionTypesDemo.java`を次の全コードへ置き換えます。

```java
public class ExceptionTypesDemo {
    // NumberFormatException（ナンバー・フォーマット・エクセプション）は、
    // RuntimeException系の例外なのでunchecked例外
    // 数値に変換できない文字列を数値へ変換しようとしたときに発生する
    static int parseQuantity(String raw) {
        // Integer.parseInt(...)は、String型の文字列をint型の整数へ変換するメソッド
        // rawが「10」なら整数の10を返すが、「abc」は整数に変換できないため、
        // NumberFormatExceptionが発生する
        // unchecked例外なので、メソッド宣言へのthrowsの記述は必須ではない
        return Integer.parseInt(raw);
    }

    public static void main(String[] args) {
        // unchecked例外のcatchは文法上の必須条件ではない
        // 今回は入力間違いを案内して正常に処理を終えるため、設計上の判断でcatchする
        try {
            // 数値に変換できない「abc」を実引数として渡し、意図的に例外を発生させる
            int q = parseQuantity("abc");

            // 直前で例外が発生すると処理がcatchへ移るため、この行は実行されない
            System.out.println("quantity=" + q);
        } catch (NumberFormatException e) { // 発生した例外を変数eで受け取る
            // e.getClass()で発生した例外のクラス情報を取得する
            // getSimpleName()でパッケージ名を除いた短いクラス名を取得する
            // 今回取得できる文字列は「NumberFormatException」
            System.out.println("unchecked例外を捕捉: " + e.getClass().getSimpleName());
        }
    }
}
```

コンパイルして実行:

```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
java ExceptionTypesDemo
```

期待出力例:
```text
unchecked例外を捕捉: NumberFormatException
```

ここまでの結果を比較します。

| 確認したコード | コンパイル | 実行 |
| --- | --- | --- |
| Step 1-1: checked例外を未処理 | 失敗 | 実行できない |
| Step 1-2: checked例外を`throws`と`catch`で処理 | 成功 | `catch`の処理が実行される |
| Step 2-1: unchecked例外を未処理 | 成功 | 例外で停止する |
| Step 2-2: unchecked例外を任意に`catch` | 成功 | `catch`の処理が実行される |

### Step 3: `throws` で上位へ伝播する設計を確認（仕上げ）

Step 1では`loadDriver()`から`main()`へ例外を1段階伝えました。ここでは、実務のようにメソッドを複数段階呼び出す場合を想定し、例外が次の順番で伝わることを確認します。

```text
loadMode("")でConfigExceptionを発生
  ↓ loadMode()のthrows ConfigException
startApplication()へ伝わる
  ↓ startApplication()のthrows ConfigException
main()へ伝わる
  ↓ main()のcatch
設定エラーメッセージを表示
```

`ExceptionTypesDemo.java`を次の全コードへ置き換えます。

```java
// ===== Step 3 完成コード =====
// RuntimeExceptionではなくExceptionを継承すると、独自のchecked例外を作成できる
// ConfigExceptionは、設定値に問題があることを表すために作成する例外クラス
class ConfigException extends Exception {
    // 例外発生時に表示するメッセージを受け取るコンストラクタ
    ConfigException(String message) {
        // 親クラスExceptionのコンストラクタへメッセージを渡して保存する
        // 保存したメッセージは、後でgetMessage()を使って取得できる
        super(message);
    }
}

public class ExceptionTypesDemo {
    // 受け取った設定値を検証し、整形した文字列を返すメソッド
    // ConfigExceptionはchecked例外なので、呼び出し側へ任せる場合はthrowsの宣言が必要
    static String loadMode(String value) throws ConfigException {
        // valueがnull、または空文字・空白だけの場合は、設定値が未設定と判断する
        if (value == null || value.isBlank()) {
            // throw new ...は、この場所で例外を意図的に発生させる書き方
            // 「mode が未設定です」はConfigExceptionのコンストラクタへ渡される
            throw new ConfigException("mode が未設定です");
        }

        // trim()で前後の空白を除き、toUpperCase()で英字を大文字にして返す
        // 例えば「 prod 」を渡した場合は「PROD」を返す
        return value.trim().toUpperCase();
    }

    // アプリケーションの開始処理を想定した中間メソッド
    // loadMode()から伝わるConfigExceptionをここではcatchせず、
    // throwsで、このメソッドを呼び出したmain()へさらに伝える
    static void startApplication() throws ConfigException {
        // 空文字を渡して、loadMode()内で意図的にConfigExceptionを発生させる
        String mode = loadMode("");

        // 直前で例外が発生するため、今回はこの行は実行されない
        System.out.println("mode=" + mode);
    }

    // 次のミニ演習で、unchecked例外と比較するために使用するメソッド
    // 受け取った整数が正数かどうかを検証するメソッド
    // IllegalArgumentExceptionはRuntimeException系のunchecked例外なので、
    // メソッド宣言へのthrowsの記述は必須ではない
    static int requirePositive(int n) {
        // 0以下は正数ではないため、不正な値と判断する
        if (n <= 0) {
            // IllegalArgumentExceptionは、メソッドへ不適切な値が渡されたことを表す例外
            throw new IllegalArgumentException("n は正数が必要です");
        }

        // 正数の場合は、受け取った値をそのまま返す
        return n;
    }

    public static void main(String[] args) {
        try {
            // startApplication()から伝わるConfigExceptionを受け取る可能性がある
            startApplication();
        } catch (ConfigException e) { // startApplication()から伝わった例外を受け取る
            // getMessage()は、ConfigExceptionを作成したときに渡した
            // 「mode が未設定です」という例外メッセージを取得するメソッド
            System.out.println("設定エラー: " + e.getMessage());
        }
    }
}
// ===== Step 3 完成コードここまで =====
```

コンパイルして実行:

```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
java ExceptionTypesDemo
```

期待出力例:
```text
設定エラー: mode が未設定です
```

この出力により、`loadMode()`で発生した例外が`startApplication()`を通過し、`main()`の`catch`まで伝わったことを確認できます。`startApplication()`は例外を解決できないため、`catch`せずに`throws`で呼び出し側へ任せています。

---

## 5. ミニ演習（10分）

Step 3で完成した`ExceptionTypesDemo.java`を基準に、レベル1からレベル3まで順番に進めてください。コンパイルエラーや未処理例外を確認する一時変更は、指示されたタイミングで元へ戻します。

### レベル1（基本）

1. `startApplication()`のメソッド宣言から`throws ConfigException`だけを一時的に外す。
2. `javac -encoding UTF-8 ExceptionTypesDemo.java`を実行する。
3. `loadMode("")`から伝わるchecked例外を`catch`も`throws`もしていないため、コンパイルエラーになることを確認する。
4. 確認後は、`startApplication()`の宣言を`throws ConfigException`付きへ戻してからレベル2へ進む。

期待状態:

- checked例外を呼び出した中間メソッドにも、`catch`または`throws`が必要だと説明できる
- `startApplication()`の`throws`が、`main()`へ例外を伝えるために必要だと説明できる

### レベル2（拡張）

1. レベル1で外した`throws ConfigException`が元に戻っていることを確認する。
2. `main()`のchecked例外を扱う`try` / `catch`より後で、`requirePositive(0)`を`catch`せずに呼ぶ。
3. コンパイルに成功することを確認してから実行し、`IllegalArgumentException`で停止することを確認する。
4. 確認後、`requirePositive(0)`の呼び出しを別の`try` / `catch`で囲み、`IllegalArgumentException: n は正数が必要です`と表示する。

期待状態:

- `IllegalArgumentException`はunchecked例外なので、`catch`がなくてもコンパイルできると説明できる
- 今回はプログラムを途中で停止させず、入力エラーを表示するという設計上の判断で`catch`していると説明できる

### レベル3（実務）

1. レベル2まで使用した`ConfigException`、`loadMode()`、`startApplication()`、`requirePositive()`は残す。
2. `main()`を、`ConfigException`の`catch`を使わず、`throws ConfigException`でJava実行環境へ伝える構成に置き換える。
3. `main()`から`startApplication()`を呼び、実行時に`ConfigException`のスタックトレースが表示されてプログラムが終了することを確認する。

期待状態:
- `catch` ではなく `throws` で上位へ委譲できる
- `main()`からさらに伝わった例外はJava実行環境へ到達し、プログラムが終了すると説明できる
- レベル3は例外設計の別案を確認するため、レベル2の`main`を置き換える

---

## 6. つまずきポイント
- checked例外なのに `catch` / `throws` を書かない
  -> コンパイルエラーになる
- unchecked例外を「絶対に処理不要」と誤解
  -> 入力境界では `catch` やバリデーションで制御する
- `throws` を付ける場所を迷う
  -> そのメソッドで回復できない場合に上位へ委譲する
