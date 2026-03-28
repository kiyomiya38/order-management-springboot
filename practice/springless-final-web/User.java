public class User {
    // 外部から直接変更させない
    private final String username;
    private final String role;

    // 引数を受け取り、thisでフィールドへ保存する
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}