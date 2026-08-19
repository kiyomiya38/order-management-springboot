package model;

public class AccountInspector {
    public static void main(String[] args) {
        Account a = new Account();
        System.out.println(a.id); // public
        System.out.println(a.points); // 同一パッケージなので可
        System.out.println(a.status); // 無指定なので可
        System.out.println(a.publicInfo()); // public
        System.out.println(a.protectedInfo()); // 同一パッケージなので可
        System.out.println(a.packageInfo()); // 同一パッケージなので可
        System.out.println(a.debugSecret()); // private情報は公開メソッド経由なら可
        // System.out.println(a.secret); // private なので不可
    }
}