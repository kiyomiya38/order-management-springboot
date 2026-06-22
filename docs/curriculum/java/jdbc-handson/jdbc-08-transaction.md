# JDBC-08 ハンズオン: トランザクション（commit / rollback）

対応参考資料: `J4_02_5_トランザクション.pdf`

## 1. この資料のゴール
- トランザクションの目的を説明できる
- `setAutoCommit(false)` / `commit()` / `rollback()` を実装できる
- 失敗時に整合性を保つコードを書ける

---

## 2. 事前準備
- JDBC-07 まで完了
- `sample-jdbc-schema.sql` で `point_account` 作成済み

```sql
SOURCE ~/order-management-springboot/docs/curriculum/java/jdbc-handson/sample-jdbc-schema.sql;
```

---

## 3. 先に覚えるポイント
1. 複数更新を「全成功 or 全取消」で扱うのがトランザクション
2. `setAutoCommit(false)` で自動コミットを無効化
3. 成功時 `commit()`、失敗時 `rollback()`

---

## 4. ハンズオン

目的:
- 2件更新を1トランザクションで安全に実行する

完了条件:
- ポイント移動処理を commit / rollback 付きで実装できる

作成ファイル: `~/order-management-springboot/practice/jdbc/handson08/TransactionDemo.java`

### Step 0: 作業フォルダ作成
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson08/lib
cd ~/order-management-springboot/practice/jdbc/handson08
```

### Step 1: トランザクション付きポイント移動を実装
`TransactionDemo.java` を次の内容で作成:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransactionDemo {
    private static final String URL =
        "jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection con = DriverManager.getConnection(URL, "test_user", "test_pass")) {
            printBalance(con, 1);
            printBalance(con, 2);

            transferPoint(con, 1, 2, 20);

            printBalance(con, 1);
            printBalance(con, 2);
        }
    }

    private static void transferPoint(Connection con, int fromMemberId, int toMemberId, int point) throws Exception {
        String withdrawSql = "UPDATE point_account SET point_balance = point_balance - ? WHERE member_id = ?";
        String depositSql = "UPDATE point_account SET point_balance = point_balance + ? WHERE member_id = ?";

        con.setAutoCommit(false);
        try (PreparedStatement w = con.prepareStatement(withdrawSql);
             PreparedStatement d = con.prepareStatement(depositSql)) {
            w.setInt(1, point);
            w.setInt(2, fromMemberId);
            w.executeUpdate();

            d.setInt(1, point);
            d.setInt(2, toMemberId);
            d.executeUpdate();

            con.commit();
            System.out.println("commit完了");
        } catch (Exception e) {
            con.rollback();
            System.out.println("rollback実行: " + e.getMessage());
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    private static void printBalance(Connection con, int memberId) throws Exception {
        String sql = "SELECT member_id, point_balance FROM point_account WHERE member_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println("member=" + rs.getInt("member_id") + ", point=" + rs.getInt("point_balance"));
                }
            }
        }
    }
}
```

### Step 2: 実行して commit を確認
JDBC-06 と同様に classpath 指定で実行する。

期待状態:
- member1 は -20、member2 は +20

期待出力例:
```text
commit完了
```

### Step 3: rollback を確認（仕上げ）
`transferPoint` 内で意図的に例外を投げ、ポイントが元に戻ることを確認する。

---

## 5. ミニ演習（10分）
1. 移動ポイントが負数なら例外にして rollback する。
2. 残高不足なら更新せず rollback するチェックを追加する。
3. ログに「開始/コミット/ロールバック」を出す。

---

## 6. つまずきポイント
- `setAutoCommit(false)` 忘れ
  -> 意図せず1文ごとに確定される
- catch で `rollback()` しない
  -> 中途半端な更新が残る
- finally で `setAutoCommit(true)` 戻し忘れ
  -> 後続処理に副作用が出る
