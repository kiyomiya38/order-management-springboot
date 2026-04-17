# JDBC-05 ハンズオン: PreparedStatementとプレースホルダ

対応参考資料: `J4_02_3_PreparedStatement.pdf`

## 1. この資料のゴール
- プレースホルダ `?` を使ったSQLを実装できる
- `setString` / `setInt` で安全に値をバインドできる
- SQLインジェクション対策としての意義を説明できる

---

## 2. 事前準備
- JDBC-04 まで完了

---

## 3. 先に覚えるポイント
1. SQL文字列連結は避け、`PreparedStatement` を使う
2. 型に応じて `setXxx` メソッドを使い分ける
3. プレースホルダ利用で可読性・安全性・再利用性が上がる

---

## 4. ハンズオン

目的:
- バインド変数を使った安全なSQL実行を体験する

完了条件:
- ID指定検索と名前更新を `PreparedStatement` で実装できる

作成ファイル: `~/order-management-springboot/practice/jdbc/handson05/PreparedStatementDemo.java`

### Step 0: 作業フォルダ作成
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson05/lib
cd ~/order-management-springboot/practice/jdbc/handson05
```

### Step 1: ID指定SELECTを実装
`PreparedStatementDemo.java` を次の内容で作成:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PreparedStatementDemo {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";

        try (Connection con = DriverManager.getConnection(url, "test_user", "test_pass")) {
            String sql = "SELECT member_id, member_name, age FROM uzuz_member WHERE member_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, 1);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getInt("member_id") + " / " +
                                           rs.getString("member_name") + " / " +
                                           rs.getInt("age"));
                    }
                }
            }
        }
    }
}
```

### Step 2: UPDATEを実装
Step1 の `main` 内に以下を追加:

```java
String updateSql = "UPDATE uzuz_member SET member_name = ? WHERE member_id = ?";
try (PreparedStatement ps = con.prepareStatement(updateSql)) {
    ps.setString(1, "TanakaUpdated");
    ps.setInt(2, 1);
    int count = ps.executeUpdate();
    System.out.println("update件数=" + count);
}
```

### Step 3: 文字列連結方式との違いを確認（仕上げ）
```java
// NG例（教材内コメントのみ。実運用で使わない）:
// String unsafeSql = "SELECT * FROM uzuz_member WHERE member_name = '" + userInput + "'";
```

---

## 5. ミニ演習（10分）
1. `member_id` を外部引数（`args[0]`）で受け取り検索する。
2. 年齢更新SQLを追加し、`setInt` を使って更新する。
3. 同じSQLを複数回呼ぶケースで、`PreparedStatement` の利点を説明する。

---

## 6. つまずきポイント
- `Parameter index out of range`
  -> `?` の個数と `setXxx` 回数が一致しているか確認
- 型不一致エラー
  -> `setString` / `setInt` をカラム型に合わせる
- SQLインジェクション対策不足
  -> 文字列連結方式を避ける
