public class UserAccount { // バリデーション付きのカプセル化クラス
    private String username; // ユーザー名
    private int age; // 年齢

    public String getUsername() { // username の getter
        return username; // 現在の username を返す
    }

    public void setUsername(String username) { // username の setter
        if (username == null || username.isBlank()) { // null または空白のみは不正
            throw new IllegalArgumentException("username は必須です"); // 不正値を例外で通知
        }
        this.username = username.trim(); // 前後空白を除去して保存
    }

    public int getAge() { // age の getter
        return age; // 現在の age を返す
    }

    public void setAge(int age) { // age の setter
        if (age < 0 || age > 120) { // 年齢範囲チェック
            throw new IllegalArgumentException("age の範囲が不正です"); // 不正値を例外で通知
        }
        this.age = age; // 検証済み値を保存
    }
} // クラス定義の終わり