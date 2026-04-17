# JDBC-03 ハンズオン: 接続の確立と接続の解除

対応参考資料: `J4_01_4_接続の確立.pdf`, `J4_01_7_接続の解除.pdf`

## 1. この資料のゴール
- `DriverManager.getConnection` で接続を確立できる
- `Connection` を確実に `close` できる
- `try-with-resources` の基本を使える

---

## 2. 事前準備
- `test_db`, `test_user`, `test_pass` を準備済み（MySQL-02）
- Connector/J JAR を用意済み（JDBC-02）

---

## 3. 先に覚えるポイント
1. 接続URLは `jdbc:mysql://host:port/dbname?...`
2. 接続成功/失敗に関わらず `close` が必要
3. `try-with-resources` で `close` の書き漏れを防げる

---

## 4. ハンズオン

目的:
- JDBC接続と解除を最小コードで実行する

完了条件:
- 接続成功メッセージを表示し、例外なく終了できる

作成ファイル: `~/order-management-springboot/practice/jdbc/handson03/JdbcConnectionDemo.java`

### Step 0: 作業フォルダ作成
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson03/lib
cd ~/order-management-springboot/practice/jdbc/handson03
```

### Step 1: 接続と解除（finally）を実装
`JdbcConnectionDemo.java` を次の内容で作成:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnectionDemo {
    public static void main(String[] args) {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo",
                "test_user",
                "test_pass"
            );
            System.out.println("接続成功");
        } catch (ClassNotFoundException e) {
            System.out.println("ドライバ未検出: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("接続エラー: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                    System.out.println("接続解除");
                } catch (SQLException e) {
                    System.out.println("close失敗: " + e.getMessage());
                }
            }
        }
    }
}
```

### Step 2: コンパイルと実行
Windows (PowerShell):
```bash
javac -encoding UTF-8 -cp ".;lib/mysql-connector-j-8.x.x.jar" JdbcConnectionDemo.java
java -cp ".;lib/mysql-connector-j-8.x.x.jar" JdbcConnectionDemo
```

mac/linux:
```bash
javac -encoding UTF-8 -cp ".:lib/mysql-connector-j-8.x.x.jar" JdbcConnectionDemo.java
java -cp ".:lib/mysql-connector-j-8.x.x.jar" JdbcConnectionDemo
```

期待結果:
- `接続成功`
- `接続解除`

### Step 3: try-with-resources 版へ変更（仕上げ）
`Connection` の宣言を `try (...)` に移して `finally` を不要化する。

---

## 5. ミニ演習（10分）
1. DB名をわざと誤って接続失敗を確認する。
2. パスワードを誤って `SQLException` メッセージを確認する。
3. `try-with-resources` 版で `close` ログが出るように実装する。

---

## 6. つまずきポイント
- `Communications link failure`
  -> MySQL サーバ起動状態を確認
- `Access denied`
  -> ユーザー/パスワード/権限を確認
- `serverTimezone` 関連の警告
  -> URL パラメータを明示する
