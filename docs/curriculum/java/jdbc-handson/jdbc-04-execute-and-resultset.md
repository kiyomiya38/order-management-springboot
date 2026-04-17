# JDBC-04 ハンズオン: SQL文の送信と抽出結果の取得

対応参考資料: `J4_01_5_SQL文の送信.pdf`, `J4_01_6_抽出結果の取得.pdf`

## 1. この資料のゴール
- `executeUpdate` で INSERT/UPDATE/DELETE を実行できる
- `executeQuery` で SELECT を実行できる
- `ResultSet` を `while (rs.next())` で読み出せる

---

## 2. 事前準備
- JDBC-03 まで完了
- サンプルスキーマ投入済み

```sql
SOURCE ~/order-management-springboot/docs/curriculum/java/jdbc-handson/sample-jdbc-schema.sql;
```

---

## 3. 先に覚えるポイント
1. `executeUpdate` の戻り値は更新件数
2. `executeQuery` の戻り値は `ResultSet`
3. `ResultSet` はカーソルを `next()` で進める

---

## 4. ハンズオン

目的:
- SQL送信と結果取得の基本動作を実装する

完了条件:
- JDBCコードから1件追加し、一覧取得を表示できる

作成ファイル: `~/order-management-springboot/practice/jdbc/handson04/JdbcExecuteDemo.java`

### Step 0: 作業フォルダ作成
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson04/lib
cd ~/order-management-springboot/practice/jdbc/handson04
```

### Step 1: executeUpdate と executeQuery を実装
`JdbcExecuteDemo.java` を次の内容で作成:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcExecuteDemo {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";

        try (Connection con = DriverManager.getConnection(url, "test_user", "test_pass")) {
            String insertSql = "INSERT INTO uzuz_member (member_name, age, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, "Kato");
                ps.setInt(2, 27);
                ps.setString(3, "kato@example.com");
                int count = ps.executeUpdate();
                System.out.println("insert件数=" + count);
            }

            String selectSql = "SELECT member_id, member_name, age, email FROM uzuz_member WHERE deleted_flg = '0' ORDER BY member_id";
            try (PreparedStatement ps = con.prepareStatement(selectSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("member_id");
                    String name = rs.getString("member_name");
                    int age = rs.getInt("age");
                    String email = rs.getString("email");
                    System.out.println(id + " / " + name + " / " + age + " / " + email);
                }
            }
        }
    }
}
```

### Step 2: コンパイルと実行
Windows (PowerShell):
```bash
javac -encoding UTF-8 -cp ".;lib/mysql-connector-j-8.x.x.jar" JdbcExecuteDemo.java
java -cp ".;lib/mysql-connector-j-8.x.x.jar" JdbcExecuteDemo
```

mac/linux:
```bash
javac -encoding UTF-8 -cp ".:lib/mysql-connector-j-8.x.x.jar" JdbcExecuteDemo.java
java -cp ".:lib/mysql-connector-j-8.x.x.jar" JdbcExecuteDemo
```

### Step 3: UPDATE も追加（仕上げ）
同ファイルに更新処理を追記し、`executeUpdate` の戻り件数を表示する。

---

## 5. ミニ演習（10分）
1. `member_name='Kato'` を `member_name='Kato2'` に更新する。
2. `deleted_flg='1'` のデータを除外する条件を変更して確認する。
3. `SELECT` の並び順を `age DESC` に変える。

---

## 6. つまずきポイント
- `No operations allowed after statement closed`
  -> `ResultSet` / `PreparedStatement` のスコープを確認
- `Column not found`
  -> `rs.getXxx("列名")` のスペルを確認
- 追加成功なのに一覧が見えない
  -> `WHERE` 条件で除外していないか確認
