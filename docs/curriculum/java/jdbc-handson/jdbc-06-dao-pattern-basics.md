# JDBC-06 ハンズオン: DAOパターン基礎

対応参考資料: `J4_02_1_DAOとは.pdf`, `J4_02_2_DAOパターン.pdf`

## 1. この資料のゴール
- DAOパターンの目的を説明できる
- DBアクセス処理をDAOクラスへ分離できる
- 呼び出し側（Service/Main）との責務分離を実装できる

---

## 2. 事前準備
- JDBC-05 まで完了

---

## 3. 先に覚えるポイント
1. DAOは「DBアクセス専用クラス」
2. BusinessLogic/Service は業務ルール、DAOはSQL担当
3. 変更影響をDAOに閉じ込めるのが狙い

---

## 4. ハンズオン

目的:
- Main / Service / DAO を分離した最小構成を作る

完了条件:
- DAO経由で会員一覧を取得して表示できる

作業フォルダ: `~/order-management-springboot/practice/jdbc/handson06`

### Step 0: 作業フォルダ作成
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson06/lib
cd ~/order-management-springboot/practice/jdbc/handson06
```

### Step 1: DAOクラス作成
作成ファイル: `MemberDao.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {
    public List<String> findActiveMemberNames() throws Exception {
        List<String> names = new ArrayList<>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";

        try (Connection con = DriverManager.getConnection(url, "test_user", "test_pass");
             PreparedStatement ps = con.prepareStatement(
                 "SELECT member_name FROM uzuz_member WHERE deleted_flg = '0' ORDER BY member_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("member_name"));
            }
        }
        return names;
    }
}
```

### Step 2: ServiceとMain作成
作成ファイル: `MemberService.java`

```java
import java.util.List;

public class MemberService {
    private final MemberDao memberDao = new MemberDao();

    public List<String> listNames() throws Exception {
        return memberDao.findActiveMemberNames();
    }
}
```

作成ファイル: `DaoPatternDemo.java`

```java
public class DaoPatternDemo {
    public static void main(String[] args) throws Exception {
        MemberService service = new MemberService();
        for (String name : service.listNames()) {
            System.out.println(name);
        }
    }
}
```

### Step 3: 実行（仕上げ）
Windows (PowerShell):
```bash
javac -encoding UTF-8 -cp ".;lib/mysql-connector-j-8.x.x.jar" *.java
java -cp ".;lib/mysql-connector-j-8.x.x.jar" DaoPatternDemo
```

mac/linux:
```bash
javac -encoding UTF-8 -cp ".:lib/mysql-connector-j-8.x.x.jar" *.java
java -cp ".:lib/mysql-connector-j-8.x.x.jar" DaoPatternDemo
```

---

## 5. ミニ演習（10分）
1. DAOに `findById(int id)` を追加する。
2. Service側で存在しないIDの時にメッセージ分岐を追加する。
3. SQLを変更しても Main 側の変更が不要なことを確認する。

---

## 6. つまずきポイント
- DAOに業務ロジックを混ぜる
  -> DAOはDBアクセスに限定する
- Mainから直接SQLを書き始める
  -> Service/DAO経由で責務を分離する
