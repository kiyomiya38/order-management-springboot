# JDBC-02 ハンズオン: JDBC環境設定（Connector/J と classpath）

対応参考資料: `J4_01_2_JDBCの環境設定.pdf`, `J4_01_4_接続の確立.pdf`

## 1. この資料のゴール
- JDBC Connector/J を配置できる
- `classpath` を指定して `javac` / `java` を実行できる
- `Class.forName("com.mysql.cj.jdbc.Driver")` でドライバロード確認ができる

---

## 2. 事前準備
- MySQL 8 系がローカル起動している
- Connector/J (`mysql-connector-j-8.x.x.jar`) を入手済み

---

## 3. 先に覚えるポイント
1. JDBCドライバの JAR が classpath にないと接続できない
2. MySQL 8 系のドライバクラス名は `com.mysql.cj.jdbc.Driver`
3. classpath 区切りは Windows `;`、mac/linux `:`

---

## 4. ハンズオン

目的:
- JDBCドライバ読み込みの最小動作を確認する

完了条件:
- `DriverLoadCheck` の実行で「Driver loaded」が表示される

作業フォルダ: `~/order-management-springboot/practice/jdbc/handson02`

### Step 0: フォルダ作成とJAR配置
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson02/lib
cd ~/order-management-springboot/practice/jdbc/handson02
```

`lib` 配下に `mysql-connector-j-8.x.x.jar` を配置する。

### Step 1: 動作確認コード作成
作成ファイル: `DriverLoadCheck.java`

```java
public class DriverLoadCheck {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        }
    }
}
```

### Step 2: コンパイル
Windows (PowerShell):
```bash
javac -encoding UTF-8 -cp ".;lib/mysql-connector-j-8.x.x.jar" DriverLoadCheck.java
```

mac/linux:
```bash
javac -encoding UTF-8 -cp ".:lib/mysql-connector-j-8.x.x.jar" DriverLoadCheck.java
```

### Step 3: 実行（仕上げ）
Windows (PowerShell):
```bash
java -cp ".;lib/mysql-connector-j-8.x.x.jar" DriverLoadCheck
```

mac/linux:
```bash
java -cp ".:lib/mysql-connector-j-8.x.x.jar" DriverLoadCheck
```

期待結果:
- `Driver loaded`

---

## 5. ミニ演習（10分）
1. classpath から JAR を外して実行し、失敗メッセージを確認する。
2. JAR のバージョンを変えた場合の実行可否を確認する。
3. `Class.forName` の文字列を誤ったクラス名にして失敗を確認する。

---

## 6. つまずきポイント
- `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
  -> classpath に JAR が入っていない
- コンパイルは通るが実行失敗
  -> `javac` と `java` 両方に classpath 指定が必要
