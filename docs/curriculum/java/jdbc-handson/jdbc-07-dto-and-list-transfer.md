# JDBC-07 ハンズオン: DTOと複数データの受け渡し

対応参考資料: `J4_02_4_DTO.pdf`, `J4_02_6_複数データの受け渡し.pdf`

## 1. この資料のゴール
- DTO（Data Transfer Object）の目的を説明できる
- DAOから `List<DTO>` を返せる
- Main層で DTO の中身を使って表示できる

---

## 2. 事前準備
- JDBC-06 まで完了

---

## 3. 先に覚えるポイント
1. DTOは1レコード分のデータを運ぶ箱
2. DAOは `ResultSet` を DTO に詰め替えて返す
3. 複数行は `List<DTO>` で受け渡す

---

## 4. ハンズオン

目的:
- 文字列だけでなく構造化データを層間で受け渡す

完了条件:
- `MemberDto` を使って一覧表示できる

作業フォルダ: `~/order-management-springboot/practice/jdbc/handson07`

### Step 0: 作業フォルダ作成
```bash
mkdir -p ~/order-management-springboot/practice/jdbc/handson07/lib
cd ~/order-management-springboot/practice/jdbc/handson07
```

### Step 1: DTO作成
作成ファイル: `MemberDto.java`

```java
public class MemberDto {
    private int memberId;
    private String memberName;
    private int age;
    private String email;

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

### Step 2: DAOで `List<MemberDto>` を返す
作成ファイル: `MemberDao.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {
    public List<MemberDto> findAll() throws Exception {
        List<MemberDto> list = new ArrayList<>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/test_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";

        String sql = "SELECT member_id, member_name, age, email FROM uzuz_member WHERE deleted_flg = '0' ORDER BY member_id";
        try (Connection con = DriverManager.getConnection(url, "test_user", "test_pass");
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MemberDto dto = new MemberDto();
                dto.setMemberId(rs.getInt("member_id"));
                dto.setMemberName(rs.getString("member_name"));
                dto.setAge(rs.getInt("age"));
                dto.setEmail(rs.getString("email"));
                list.add(dto);
            }
        }
        return list;
    }
}
```

### Step 3: Mainから一覧表示（仕上げ）
作成ファイル: `DtoDemo.java`

```java
import java.util.List;

public class DtoDemo {
    public static void main(String[] args) throws Exception {
        MemberDao dao = new MemberDao();
        List<MemberDto> members = dao.findAll();
        for (MemberDto m : members) {
            System.out.println(m.getMemberId() + " / " + m.getMemberName() + " / " + m.getAge() + " / " + m.getEmail());
        }
    }
}
```

実行:
- JDBC-06 と同様に Connector/J を classpath 指定して実行

---

## 5. ミニ演習（10分）
1. DTOに `deletedFlg` を追加してマッピングする。
2. DAOに `findByNamePrefix(String prefix)` を追加する。
3. Main側で `age >= 25` のデータだけ表示する分岐を追加する。

---

## 6. つまずきポイント
- DTOのsetter呼び忘れで `null` 表示
  -> `ResultSet` からの代入箇所を確認
- List追加漏れ
  -> `list.add(dto)` を `while` 内に置く
