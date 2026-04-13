public class ConstructorDiDemo { // このサンプルを実行するメインクラス
    static class MessageService { // メッセージ作成だけを担当する小さなクラス
        String createMessage(String name) { // 呼び出し元から受け取った名前で挨拶文を作る
            return "Hello, " + name; // 文字列を連結して結果を返す
        } // createMessageメソッドの終わり
    } // MessageServiceクラスの終わり

    static class GreetingControllerLike { // Serviceを利用する側（Controller風）のクラス
        private final MessageService messageService; // private: クラス内専用 / final: 再代入しない

        GreetingControllerLike(MessageService messageService) { // コンストラクタ: new時に1回だけ呼ばれる初期化処理
            this.messageService = messageService; // 引数で受け取ったServiceを自分のフィールドへ保存する
        } // コンストラクタの終わり

        String hello(String name) { // 呼び出し窓口メソッド: 受け取ったnameで挨拶を返す
            return messageService.createMessage(name); // 文字列作成処理はServiceへ委譲する
        } // helloメソッドの終わり
    } // GreetingControllerLikeクラスの終わり

    public static void main(String[] args) { // Javaプログラムの実行開始地点
        MessageService service = new MessageService(); // Serviceのインスタンスを生成する
        GreetingControllerLike controller = new GreetingControllerLike(service); // コンストラクタでServiceを注入してControllerを作る
        System.out.println(controller.hello("Shinesoft")); // 実行結果を標準出力へ表示する
    } // mainメソッドの終わり
} // ConstructorDiDemoクラスの終わり