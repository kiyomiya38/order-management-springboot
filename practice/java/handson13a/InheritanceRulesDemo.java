class Worker { // 親クラス
    final void submitReport() { // final メソッド: 子クラスで上書き（override）できない
        System.out.println("report submitted"); // レポート提出完了メッセージを表示
    } // submitReport メソッドの終わり
} // Worker クラス定義の終わり

class Manager extends Worker { // 子クラス: Worker を継承
    // 任意確認: 下の2行は「final メソッドの上書きエラー」を確認するときだけコメント解除する
    // @Override // 親メソッドを上書きしていることを明示するアノテーション
    // void submitReport() {} // final メソッドを上書きしようとしてコンパイルエラーになる
} // Manager クラス定義の終わり

final class FixedRole { // final クラス: このクラス自体を継承できない
} // FixedRole クラス定義の終わり

class DerivedRole extends FixedRole {} // final クラスを extends するとコンパイルエラー

public class InheritanceRulesDemo { // 実行クラス
    public static void main(String[] args) { // プログラム開始地点
        Manager m = new Manager(); // Manager のインスタンスを生成
        m.submitReport(); // 親クラスの final メソッドをそのまま利用する
    } // main メソッドの終わり
} // 実行クラス定義の終わり