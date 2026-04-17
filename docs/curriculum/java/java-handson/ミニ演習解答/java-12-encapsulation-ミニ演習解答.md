# Java-12 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-12-encapsulation.md`

## ミニ演習解答
```java
public class UserAccount {
    private String username;
    private int age;
    private String email;

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username は必須です");
        }
        this.username = username.trim();
    }

    public void setAge(int age) {
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("age の範囲が不正です: " + age);
        }
        this.age = age;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("email 形式が不正です: " + email);
        }
        this.email = email.trim();
    }
}
```

確認コード:
- `setUsername("   ")` -> 例外発生  
- `setAge(130)` -> 例外発生  
- `setEmail("userexample.com")` -> 例外発生
