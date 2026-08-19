package model;

public class Account {
    public String id = "A-001"; // どこからでも可
    protected int points = 100; // 同一パッケージ + サブクラス可
    String status = "ACTIVE"; // 無指定: 同一パッケージのみ
    private String secret = "internal"; // 同一クラスのみ

    public String publicInfo() {
        return "public";
    }

    protected String protectedInfo() {
        return "protected";
    }

    String packageInfo() {
        return "package-private";
    }

    private String privateInfo() {
        return "private";
    }

    public String debugSecret() { // 同一クラス内なので private へアクセス可能
        return privateInfo() + ":" + secret;
    }
}