class BaseGreetingService { // 親クラス: 挨拶メソッドの基本形を持つ
    String greet(String name) { // 親クラス側の greet
        return "Hi, " + name;
    }
}

class FriendlyGreetingService extends BaseGreetingService { // 子クラス: 親を継承する
    @Override // 親クラスの greet を上書きしていることを示す
    String greet(String name) {
        return "Hello, " + name; // 子クラス独自の挨拶に変更
    }
}

public class OverrideDemo { // 実行クラス
    public static void main(String[] args) { // 実行入口
        BaseGreetingService service = new FriendlyGreetingService(); // 親型で受けても子の実装が使われる
        System.out.println(service.greet("Shinesoft"));
    }
}
