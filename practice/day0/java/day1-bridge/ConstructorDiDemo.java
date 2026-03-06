// このファイル全体は「コンストラクタで依存を受け取る形」を体験するためのサンプルです。
// Step 1では、意図的に trim()/isBlank() は使わず、DIの形だけに集中します。
public class ConstructorDiDemo { // 実行用クラス（mainメソッドを持つ）

    // 文字列メッセージを作るだけの小さなサービスクラス
    static class MessageService {

        // 名前を受け取って挨拶文を返すメソッド
        String createMessage(String name) {
            // Step 1では単純連結だけにして、文字列ルールはStep 2へ分離する
            return "Hello, " + name;
        }
    }

    // Controller風のクラス。Serviceを利用する側
    static class GreetingControllerLike {

        // private: このクラスの中だけで使う / final: 一度代入したら再代入しない
        private final MessageService messageService;

        // これが「コンストラクタ」
        // クラス名と同じ名前で、newした直後に1回だけ呼ばれる初期化処理です。
        // 引数で受け取った service を、自分のフィールドへ保存します。
        GreetingControllerLike(MessageService messageService) {
            this.messageService = messageService; // this は「このインスタンス自身」を指す
        }

        // 呼び出し元からnameを受け取り、Serviceへ処理を委譲して結果を返す
        String hello(String name) {
            return messageService.createMessage(name);
        }
    }

    // Java実行の入口
    public static void main(String[] args) {
        // 1) Serviceの実体を作る
        MessageService service = new MessageService();

        // 2) Controllerを作るときに、Serviceを引数で渡す
        //    ここでコンストラクタが呼ばれ、messageServiceフィールドに保存される
        GreetingControllerLike controller = new GreetingControllerLike(service);

        // 3) 名前を渡して挨拶を表示する
        System.out.println(controller.hello("Shinesoft"));
    }
}
