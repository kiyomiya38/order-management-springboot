public class UserAccount { // バリデーション付きのカプセル化クラス
    private String username; // ユーザー名
    private int age; // 年齢

    public String getUsername() { // username の getter
        return username; // 現在の username を返す
    }

    public void setUsername(String username) { // username の setter
        if (username == null || username.isBlank()) { // ここが不正検知（バリデーション）：null や空白だけの入力を見つける
            throw new IllegalArgumentException("username は必須です"); // ここで例外を発生：この setter の処理を中断し、呼び出し元へエラーを通知
        }
        this.username = username.trim(); // 前後空白を除去して保存
    }

    public int getAge() { // age の getter
        return age; // 現在の age を返す
    }

    public void setAge(int age) { // age の setter
        if (age < 0 || age > 120) { // ここが不正検知（バリデーション）：年齢が 0〜120 の範囲か確認
            throw new IllegalArgumentException("age の範囲が不正です"); // ここで例外を発生：この setter の処理を中断し、呼び出し元へエラーを通知
        }
        this.age = age; // 検証済み値を保存
    }

    // レベル3で追記
    private String email;

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("email 形式が不正です: " + email);
        }
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
} // クラス定義の終わり