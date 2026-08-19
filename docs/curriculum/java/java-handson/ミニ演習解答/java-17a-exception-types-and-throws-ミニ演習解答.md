# Java-17A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-17a-exception-types-and-throws.md`

## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。
- checked例外とunchecked例外は、`catch`の有無ではなく「未処理のときにコンパイルできるか」で区別します。
- `ClassNotFoundException`は`RuntimeException`を継承していないためchecked例外、`NumberFormatException`は`RuntimeException`系のためunchecked例外です。
- 異なる例外を使うのは、1つの例外クラスがcheckedとuncheckedの両方になることはないためです。

## レベル1（基本）解答

Step 3完成コードにある`startApplication()`の宣言を、一時的に次の内容へ変更します。変更するのは`throws ConfigException`を外す部分だけです。

```java
// ===== レベル1で一時変更: throws ConfigExceptionを外す =====
static void startApplication() {
    // loadMode("")からchecked例外が伝わるが、
    // このメソッドにはcatchもthrowsもないためコンパイルエラーになる
    String mode = loadMode("");
    System.out.println("mode=" + mode);
}
```

コンパイル:

```bash
javac -encoding UTF-8 ExceptionTypesDemo.java
```

期待結果の要点:

```text
例外ConfigExceptionは報告されません。スローするには、捕捉または宣言する必要があります
```

エラーメッセージの細部は環境により異なります。`loadMode()`の`throws`は、「呼び出したメソッドも対処する必要がある」ことを意味します。

確認後は、次のように`throws ConfigException`を戻します。

```java
// ===== レベル1の一時変更を元へ戻す =====
static void startApplication() throws ConfigException {
    String mode = loadMode("");
    System.out.println("mode=" + mode);
}
```

## レベル2（拡張）解答

最初に、`main()`を次の全コードへ変更して、unchecked例外を`catch`しない場合を確認します。

```java
// ===== レベル2で一時変更: main()の全コード =====
public static void main(String[] args) {
    try {
        startApplication();
    } catch (ConfigException e) {
        System.out.println("設定エラー: " + e.getMessage());
    }

    // IllegalArgumentExceptionはunchecked例外なので、
    // catchもthrowsも書かなくてもコンパイルできる
    System.out.println(requirePositive(0));
}
```

コンパイルは成功します。実行すると、`startApplication()`のchecked例外は最初の`catch`で処理されますが、その後のunchecked例外は処理されていないため停止します。

```text
設定エラー: mode が未設定です
Exception in thread "main" java.lang.IllegalArgumentException: n は正数が必要です
```

確認後は、`main()`を次の全コードへ変更し、設計上の判断でunchecked例外も`catch`します。

```java
// ===== レベル2完了時のmain()全コード =====
public static void main(String[] args) {
    try {
        startApplication();
    } catch (ConfigException e) {
        System.out.println("設定エラー: " + e.getMessage());
    }

    // unchecked例外のcatchは必須ではないが、
    // 入力エラーでプログラムを停止させず案内するために追加する
    try {
        System.out.println(requirePositive(0));
    } catch (IllegalArgumentException e) {
        System.out.println("IllegalArgumentException: " + e.getMessage());
    }
}
```

実行結果:

```text
設定エラー: mode が未設定です
IllegalArgumentException: n は正数が必要です
```

## レベル3（実務）解答

クラス内の`loadMode()`、`startApplication()`、`requirePositive()`は残し、`main()`から`ConfigException`の`catch`を外します。代わりに`main()`へ`throws ConfigException`を書き、Java実行環境へ伝えます。

### レベル3完了時の全コード

```java
// RuntimeExceptionではなくExceptionを継承して、独自のchecked例外を作る
class ConfigException extends Exception {
    ConfigException(String message) {
        super(message); // 親クラスExceptionへメッセージを渡す
    }
}

public class ExceptionTypesDemo {
    // checked例外をこのメソッド内でcatchせず、呼び出し側へ任せる
    static String loadMode(String value) throws ConfigException {
        if (value == null || value.isBlank()) { // null、空文字、空白だけなら未設定
            throw new ConfigException("mode が未設定です"); // この場所で例外を発生させる
        }
        return value.trim().toUpperCase(); // 前後の空白を除き、大文字へ変換する
    }

    // loadMode()から伝わるchecked例外をcatchせず、main()へさらに伝える
    static void startApplication() throws ConfigException {
        String mode = loadMode(""); // ConfigExceptionが発生する
        System.out.println("mode=" + mode); // 例外発生後なので実行されない
    }

    // IllegalArgumentExceptionはRuntimeException系のunchecked例外なのでthrowsは必須ではない
    static int requirePositive(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n は正数が必要です");
        }
        return n;
    }

    // ===== レベル3で変更: catchせず、throwsでmainの呼び出し元へ伝える =====
    // mainを起動したJava実行環境へConfigExceptionを伝える
    public static void main(String[] args) throws ConfigException {
        // startApplication()から伝わった例外をcatchせず、main()の外へさらに伝える
        startApplication();
    }
    // ===== レベル3で変更ここまで =====
}
```

補足:
- checked例外（`ConfigException`）は `catch` か `throws` が必須。
- `loadMode()`で発生した例外は、`startApplication()`、`main()`の順に伝わる。
- この構成では`ConfigException`が`main()`の呼び出し元であるJava実行環境へ到達し、プログラムが終了する。

実行結果の一部:

```text
Exception in thread "main" ConfigException: mode が未設定です
```

スタックトレースの行番号などは環境により異なります。レベル3では`main()`にも`throws`を書いたためコンパイルは成功しますが、最終的に例外を`catch`する処理がないため実行時に終了します。
