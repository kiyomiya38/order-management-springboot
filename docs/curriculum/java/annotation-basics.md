# Java アノテーション基礎ガイド

## 1. この資料の目的
対象:
- アノテーションが初めての人
- `@Controller` や `@GetMapping` の意味が曖昧な人
- 「importすれば呼べるのでは？」と感じている人

この資料のゴール:
- アノテーションを「処理」ではなく「メタ情報（目印）」として説明できる
- `@interface`, `@Retention`, `@Target` の役割を説明できる
- Springでよく見るアノテーションの読み方が分かる

前提補助資料（必要な場合）:
- 継承が曖昧な場合は先に `docs/curriculum/java/inheritance-only.md` を実施する

---

## 2. アノテーションとは何か
アノテーションは、コードに付ける「意味ラベル」です。

例:
- このクラスはControllerとして扱う
- このメソッドはGET `/hello` に対応する
- この引数はリクエストパラメータ `name` から受け取る

重要:
- アノテーション自体は処理を実行しない
- 読み取る側（コンパイラ、フレームワーク、リフレクション）がいて初めて効果が出る

---

## 3. `import` との違い
| 項目 | `import` | アノテーション |
|---|---|---|
| 役割 | クラス名を短く書けるようにする | クラス/メソッド/引数に意味を付ける |
| 影響 | 書き方（記述量） | 読み取り側の動作（設定・登録・検証） |
| 例 | `import java.util.List;` | `@GetMapping("/hello")` |

補足:
- `import` は「使えるようにする宣言」
- アノテーションは「どう扱ってほしいかの宣言」

---

## 4. まずは標準アノテーション
この章は、受講者がファイルを自分で作成して理解する形式です。

### Step 0: 作業フォルダを作る
```bash
cd ~/order-management-springboot/practice/java/spring-bridge
mkdir -p standard-annotations
cd standard-annotations
```

### 4-1. `@Override` を体験する
作成ファイル: `~/order-management-springboot/practice/java/spring-bridge/standard-annotations/OverrideDemo.java`
このStepでは `interface` / `implements` は使わず、`extends`（継承）だけで `@Override` を確認します。

```java
class BaseGreetingService { // 親クラス: 挨拶メソッドの基本形を持つ
    String greet(String name) { // 親クラス側の greet
        return "Hi, " + name;
    }
}

class FriendlyGreetingService extends BaseGreetingService { // 子クラス: 親を継承する
    @Override // 親クラスの greet を上書きしていることを示す
    String greet(String name) {
        return "Hello, " + name; // 子クラス独自の挨拶に変更
    }
}

public class OverrideDemo { // 実行クラス
    public static void main(String[] args) { // 実行入口
        BaseGreetingService service = new FriendlyGreetingService(); // 親型で受けても子の実装が使われる
        System.out.println(service.greet("Shinesoft"));
    }
}
```

コード解説:
- `@Override` は「このメソッドは上書きです」と宣言する
- `extends` は「親クラスを引き継ぐ」キーワード
- メソッド名を間違えるとコンパイルエラーになるので、実装ミス防止になる

実行:
```bash
javac -encoding UTF-8 OverrideDemo.java
java OverrideDemo
```

期待出力:
```text
Hello, Shinesoft
```

1分ミニ改造:
- `greet` を `grete` にわざと変え、`@Override` を残したままコンパイルしてエラーを確認する

### 4-2. `@Deprecated` を体験する
作成ファイル: `~/order-management-springboot/practice/java/spring-bridge/standard-annotations/DeprecatedDemo.java`

```java
class LegacyPriceUtil { // 旧ルールと新ルールを持つ補助クラス
    @Deprecated // このメソッドは非推奨（今後は新ルールを使う）
    static int oldDiscountPrice(int price) {
        return price - 100; // 旧割引ロジック
    }

    static int newDiscountPrice(int price) { // こちらが推奨
        return price - 200; // 新割引ロジック
    }
}

public class DeprecatedDemo { // 実行クラス
    public static void main(String[] args) { // 実行入口
        int before = 1000;

        // ここは @Deprecated メソッドを使っているので、-Xlint:deprecation で警告対象になる
        int oldPrice = LegacyPriceUtil.oldDiscountPrice(before);
        int newPrice = LegacyPriceUtil.newDiscountPrice(before);

        System.out.println("旧ルール価格: " + oldPrice);
        System.out.println("新ルール価格: " + newPrice);
    }
}
```

コード解説:
- `@Deprecated` は「今は使えるが、新規利用は避けるべき」という目印
- 実行はできるが、コンパイル時に警告を出して置き換えを促す

実行:
```bash
javac -encoding UTF-8 -Xlint:deprecation DeprecatedDemo.java
java DeprecatedDemo
```

期待:
- 実行は成功する
- コンパイル時に「非推奨APIを使っている」警告が出る

### 4-3. `@SuppressWarnings` を体験する
作成ファイル 1: `~/order-management-springboot/practice/java/spring-bridge/standard-annotations/LegacyApiForSuppress.java`

```java
public class LegacyApiForSuppress { // 警告確認用の旧APIクラス
    @Deprecated // このメソッドは非推奨
    public static String legacyMessage() {
        return "legacy";
    }
}
```

作成ファイル 2: `~/order-management-springboot/practice/java/spring-bridge/standard-annotations/SuppressWarningsDemo.java`

```java
public class SuppressWarningsDemo { // 警告の抑制を確認するサンプル
    static void callWithoutSuppress() { // 警告を抑制しない呼び出し
        System.out.println(LegacyApiForSuppress.legacyMessage()); // -Xlint:deprecation で警告対象
    }

    @SuppressWarnings("deprecation") // このメソッド内の非推奨警告だけ抑制する
    static void callWithSuppress() {
        System.out.println(LegacyApiForSuppress.legacyMessage()); // 警告を出さずに呼べる
    }

    public static void main(String[] args) { // 実行入口
        callWithoutSuppress();
        callWithSuppress();
    }
}
```

コード解説:
- `@SuppressWarnings("deprecation")` は、指定した警告だけを抑制する
- 警告を消せるが、使いすぎると問題を隠すため最小範囲に限定する

実行:
```bash
javac -encoding UTF-8 -Xlint:deprecation LegacyApiForSuppress.java SuppressWarningsDemo.java
java SuppressWarningsDemo
```

期待:
- `callWithoutSuppress` 側は警告対象（通常は警告1件）
- `callWithSuppress` 側は `@SuppressWarnings("deprecation")` で警告抑制

使い分けまとめ:
- `@Override`: 実装ミス防止
- `@Deprecated`: 非推奨の明示
- `@SuppressWarnings`: 警告抑制（最小範囲で使う）

---

## 5. 自作アノテーションの最小例
作成ファイル: `~/order-management-springboot/practice/java/spring-bridge/AnnotationBasicsMiniDemo.java`

```java
import java.lang.annotation.ElementType;      // アノテーションを「どこに付けられるか」の種類を表す
import java.lang.annotation.Retention;        // アノテーションの保持期間を指定する
import java.lang.annotation.RetentionPolicy;  // 保持期間の具体的な値（SOURCE/CLASS/RUNTIME）
import java.lang.annotation.Target;           // アノテーションの付与対象を制限する
import java.lang.reflect.Method;              // メソッド情報を実行時に取得するために使う

@Retention(RetentionPolicy.RUNTIME) // 実行時にも @Audit 情報を残す（mainで読み取るため）
@Target(ElementType.METHOD)         // @Audit はメソッドにだけ付けられるようにする
@interface Audit {                  // ここで「@Audit」という自作アノテーション型を宣言する
    String value();                 // @Audit が持つ設定値。今回は必須の文字列1つ
}

public class AnnotationBasicsMiniDemo { // アノテーションを付けて読み取る動きを確認するクラス
    @Audit("create-order")              // createOrder メソッドに監査用ラベルを付ける
    void createOrder() {                // 普通のメソッド。アノテーションは処理ではなく目印
        System.out.println("create order"); // 実際の処理（今回は表示だけ）
    }

    public static void main(String[] args) throws Exception { // 実行入口
        // 1) クラス情報から、createOrder メソッドの情報(Method)を取得する
        Method method = AnnotationBasicsMiniDemo.class.getDeclaredMethod("createOrder");

        // 2) 取得したメソッドに付いている @Audit を読み取る
        Audit audit = method.getAnnotation(Audit.class);

        // 3) @Audit の value 属性を表示する（"create-order" が出る）
        System.out.println("Audit value: " + audit.value());
    }
}
```

実行:
```bash
cd ~/order-management-springboot/practice/java/spring-bridge
javac -encoding UTF-8 AnnotationBasicsMiniDemo.java
java AnnotationBasicsMiniDemo
```

期待出力:
```text
Audit value: create-order
```

---

## 6. よく使う要素の意味
### `@interface`
- アノテーション型を定義するキーワード

### 属性（`String value();` など）
- アノテーションに持たせる設定値
- `value` は省略記法が使える  
  例: `@Audit("create-order")` は `@Audit(value = "create-order")` と同じ

### `@Retention`
- アノテーション情報をいつまで保持するか

| 設定 | 意味 |
|---|---|
| `SOURCE` | コンパイル後は消える |
| `CLASS` | `.class` には残るが実行時取得は不可 |
| `RUNTIME` | 実行時にリフレクションで取得可能 |

### `@Target`
- どこに付けられるかを制限する

| 設定例 | 付与対象 |
|---|---|
| `ElementType.TYPE` | クラス/インターフェース |
| `ElementType.METHOD` | メソッド |
| `ElementType.PARAMETER` | 引数 |
| `ElementType.FIELD` | フィールド |

---

## 7. Springでの見方
Springでよく見るアノテーションを「誰が読むか」で整理すると理解しやすいです。

| アノテーション | 誰が読むか | 何をしているか |
|---|---|---|
| `@Controller` | Spring | このクラスをWeb層として登録 |
| `@GetMapping("/hello")` | Spring | URLとメソッドを対応付け |
| `@RequestParam(name="name")` | Spring | HTTPパラメータを引数にバインド |
| `@Test` | JUnit | テストメソッドとして実行対象にする |

ポイント:
- いずれも「宣言」
- 実際の処理はSpring/JUnit側の実装が行う

---

## 8. よくある誤解
### 誤解1: 「importすれば同じでは？」
違い:
- `import` は名前解決
- アノテーションは役割宣言

### 誤解2: 「付けただけで勝手に動く？」
答え:
- 読み取る仕組みがないと動かない

### 誤解3: 「アノテーションに業務ロジックを書く？」
答え:
- 業務ロジックは通常メソッドに書く
- アノテーションは設定・目印に使う

---

## 9. 10分ミニ演習
1. `@Audit("create-order")` を `@Audit("delete-order")` に変えて再実行する  
期待: 出力文字列が切り替わる

2. `@Retention(RetentionPolicy.RUNTIME)` を `CLASS` に変えて再実行する  
期待: `getAnnotation(...)` が `null` になり、実行時取得できないことを確認できる

3. `@Target(ElementType.METHOD)` を `TYPE` に変えて、メソッドへ付与したときのコンパイルエラーを確認する

---

## 10. チェックリスト
- アノテーションは「処理」ではなく「メタ情報」と言える
- `import` とアノテーションの違いを説明できる
- `@Retention(RUNTIME)` が必要な理由を説明できる
- Springの `@Controller` / `@GetMapping` を見て「宣言である」と理解できる
