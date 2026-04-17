# JDBC-01 ハンズオン: JDBCとは（役割と接続フロー）

対応参考資料: `J4_01_1_JDBCとは.pdf`, `J4_01_3_データベース接続の概要.pdf`

## 1. この資料のゴール
- JDBC の役割（JavaからRDBMSへ接続）を説明できる
- JDBC の基本フロー（ロード→接続→SQL実行→結果取得→解除）を説明できる
- `Connection` / `Statement` / `ResultSet` の役割を説明できる

---

## 2. 事前準備
```bash
java -version
javac -version
mysql --version
```

期待状態:
- Java 17 系
- MySQL 8 系

---

## 3. 先に覚えるポイント
1. JDBC は Java標準API（`java.sql`）で DBアクセスを行う仕組み
2. 実接続には JDBCドライバ（Connector/J）が必要
3. 主要オブジェクトは `Connection`, `PreparedStatement`, `ResultSet`

---

## 4. ハンズオン

目的:
- JDBC の全体像をコードで把握する

完了条件:
- 5ステップの流れを自分の言葉で説明できる

作成ファイル: `~/order-management-springboot/practice/jdbc/handson01/JdbcFlowDemo.java`

### Step 0: 作業フォルダ作成
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson01
cd ~/order-management-springboot/practice/jdbc/handson01
```

### Step 1: JDBCの処理フローをコード化（接続はしない）
`JdbcFlowDemo.java` を次の内容で作成:

```java
public class JdbcFlowDemo {
    public static void main(String[] args) {
        System.out.println("1) JDBCドライバのロード");
        System.out.println("2) Connectionを取得してDBに接続");
        System.out.println("3) PreparedStatementでSQLを送信");
        System.out.println("4) ResultSetで抽出結果を読む");
        System.out.println("5) ResultSet/Statement/Connectionをclose");
    }
}
```

実行:
```bash
javac -encoding UTF-8 JdbcFlowDemo.java
java JdbcFlowDemo
```

### Step 2: オブジェクト役割を確認
学習メモ（コード内コメントで記録しておく）:
- `Connection`: DB接続セッション
- `PreparedStatement`: SQL実行器（プレースホルダ対応）
- `ResultSet`: SELECT結果を1行ずつ読むカーソル

### Step 3: JDBCで使う例外を確認（仕上げ）
以下を `JdbcFlowDemo.java` の `main` に追記:

```java
System.out.println("JDBCは主にSQLExceptionを扱う");
```

---

## 5. ミニ演習（10分）
1. 5ステップを図にして説明する。
2. `Statement` と `PreparedStatement` の違いを1文で書く。
3. `close` を忘れた時のリスク（接続リーク）を説明する。

---

## 6. つまずきポイント
- JDBC API と JDBCドライバの違いが曖昧
  -> APIはJava標準、ドライバはRDBMSごとの接続実装
- `ResultSet` を「配列」と誤解
  -> カーソルを進めながら読むオブジェクト
