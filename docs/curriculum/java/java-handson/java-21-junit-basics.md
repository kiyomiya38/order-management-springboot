# Java-21 ハンズオン: JUnit 5基礎（単体テスト入門）

## 1. この資料のゴール
- JUnit 5 で単体テストを実行できる
- `@Test` / `assertEquals` / `assertThrows` を使い分けできる
- 正常系と異常系の最小テストを自力で追加できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
mvn -version
```

期待状態:
- `java -version` が `17.x`
- `mvn -version` が表示される

---

## 3. 先に覚えるポイント
1. テストは「仕様どおり動くことを機械的に確認するコード」
2. `@Test` を付けたメソッドがテスト実行対象になる
3. `assertEquals(期待値, 実際値)` は一致確認
4. `assertThrows(例外型, 実行処理)` は異常系確認

### 書式の基本

#### Maven の標準フォルダ

```text
src/main/java/com/example/tax/TaxCalculator.java
src/test/java/com/example/tax/TaxCalculatorTest.java
```

ポイント:
- `src/main/java` には実装コードを置く
- `src/test/java` にはテストコードを置く
- 同じパッケージにすると、テスト対象を扱いやすい

#### JUnit 5 の import

```java
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
```

ポイント:
- `@Test` を使うために `org.junit.jupiter.api.Test` を import する
- `assertEquals` や `assertThrows` は static import すると短く書ける

#### 正常系テスト

```java
@Test
void calcWithTax_rate10Percent() {
    int actual = calculator.calcWithTax(1000, 0.10);
    assertEquals(1100, actual);
}
```

ポイント:
- `@Test` を付けたメソッドがテストとして実行される
- `actual` は実際の実行結果を入れる変数
- `assertEquals(期待値, 実際値)` の順で書く

#### 異常系テスト

```java
@Test
void calcWithTax_negativePrice_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> calculator.calcWithTax(-1, 0.10));
}
```

ポイント:
- `assertThrows` は指定した例外が発生することを確認する
- `IllegalArgumentException.class` は期待する例外型
- `() -> ...` はテスト中に実行する処理を表す

#### テスト実行

```bash
mvn test
```

ポイント:
- Maven プロジェクトのルートで実行する
- テストがすべて成功すると `BUILD SUCCESS` が表示される
- 失敗した場合は、失敗したテスト名と期待値・実際値を確認する

---

## 4. ハンズオン

目的:
- Maven + JUnitで、実装コードとテストコードを分けて検証する流れを体験する

完了条件:
- Step 4で3つのテスト（正常2件・異常1件）が通る
- Step 5完了後に`mvn test`が成功し、4つのテスト（正常2件・異常2件）が通る

作成ファイル:
- `~/order-management-springboot/practice/java/handson21/pom.xml`
- `~/order-management-springboot/practice/java/handson21/src/main/java/com/example/tax/TaxCalculator.java`
- `~/order-management-springboot/practice/java/handson21/src/test/java/com/example/tax/TaxCalculatorTest.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson21/src/main/java/com/example/tax
mkdir -p ~/order-management-springboot/practice/java/handson21/src/test/java/com/example/tax
cd ~/order-management-springboot/practice/java/handson21
```

### Step 1: `pom.xml` を作成
`pom.xml` を次の内容で作成:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>handson21-junit-basics</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>17</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.10.2</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 2: 実装クラスを作る
`TaxCalculator.java` を次の内容で作成:

```java
package com.example.tax; // パッケージ宣言

public class TaxCalculator { // 税込計算用クラス
    public int calcWithTax(int price, double rate) { // 税込価格を返す
        if (price < 0) { // 負数価格は不正
            throw new IllegalArgumentException("price must be >= 0");
        }
        if (rate < 0) { // 税率負数も不正
            throw new IllegalArgumentException("rate must be >= 0");
        }
        return (int) Math.floor(price * (1.0 + rate)); // 切り捨てで税込計算
    } // メソッド終わり
} // クラス終わり
```

### Step 3: テストクラスを作る
`TaxCalculatorTest.java` を次の内容で作成:

```java
package com.example.tax; // テスト対象と同じパッケージ

import org.junit.jupiter.api.Test; // @Test アノテーション

import static org.junit.jupiter.api.Assertions.assertEquals; // 等価比較
import static org.junit.jupiter.api.Assertions.assertThrows; // 例外検証

class TaxCalculatorTest { // TaxCalculator のテストクラス

    private final TaxCalculator calculator = new TaxCalculator(); // テスト対象

    @Test
    void calcWithTax_rate10Percent() { // 正常系: 10%課税
        int actual = calculator.calcWithTax(1000, 0.10);
        assertEquals(1100, actual);
    }

    @Test
    void calcWithTax_rate8Percent_floor() { // 正常系: 切り捨て確認
        int actual = calculator.calcWithTax(999, 0.08);
        assertEquals(1078, actual);
    }

    @Test
    void calcWithTax_negativePrice_throwsException() { // 異常系: 負数価格
        assertThrows(IllegalArgumentException.class, () -> calculator.calcWithTax(-1, 0.10));
    }
}
```

### Step 4: テストを実行
```bash
cd ~/order-management-springboot/practice/java/handson21
mvn test
```

期待出力例:
```text
BUILD SUCCESS
Tests run: 3, Failures: 0, Errors: 0
```

### Step 5: 1本追加して確認（仕上げ）
`TaxCalculatorTest.java` に以下を追加:

```java
@Test
void calcWithTax_negativeRate_throwsException() { // 異常系: 負数税率
    assertThrows(IllegalArgumentException.class, () -> calculator.calcWithTax(1000, -0.01));
}
```

再実行:
```bash
mvn test
```

期待出力例:
```text
Tests run: 4, Failures: 0, Errors: 0
```

---

## 5. 解答例

### Step 3 の意図
1. 正常系は「仕様の代表値」を2件以上置く
2. 異常系は「例外が出るべき条件」を `assertThrows` で固定する
3. テスト名は「条件_期待結果」で読むだけで意図が分かるようにする

### Step 5 で確認できること
1. 仕様追加（負数税率禁止）をテストで固定できる
2. 実装を壊したら `mvn test` が失敗して検知できる

---

## 6. つまずきポイント
- `src/test/java` ではなく `src/main/java` にテストを書いてしまう
  -> テストクラスは `src/test/java` 配下に置く
- `@Test` を付け忘れてテストが実行されない
  -> 実行対象は `@Test` メソッドのみ
- `assertEquals` の引数順を逆にして読みづらくなる
  -> `assertEquals(期待値, 実際値)` の順で統一する
