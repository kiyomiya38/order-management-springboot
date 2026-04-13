import java.lang.annotation.ElementType;      // アノテーションを「どこに付けられるか」の種類を表す
import java.lang.annotation.Retention;        // アノテーションの保持期間を指定する
import java.lang.annotation.RetentionPolicy;  // 保持期間の具体的な値（SOURCE/CLASS/RUNTIME）
import java.lang.annotation.Target;           // アノテーションの付与対象を制限する
import java.lang.reflect.Method;              // メソッド情報を実行時に取得するために使う

@Retention(RetentionPolicy.RUNTIME) // 実行時にも @Audit 情報を残す（mainで読み取るため）
@Target(ElementType.METHOD)         // @Audit はメソッドにだけ付けられるようにする
@interface Audit {                  // ここで「@Audit」という自作アノテーション型を宣言する
    String value();                 // @Audit が持つ設定値。今回は必須の文字列1つ
}

public class AnnotationBasicsMiniDemo { // アノテーションを付けて読み取る動きを確認するクラス
    @Audit("create-order")              // createOrder メソッドに監査用ラベルを付ける
    void createOrder() {                // 普通のメソッド。アノテーションは処理ではなく目印
        System.out.println("create order"); // 実際の処理（今回は表示だけ）
    }

    public static void main(String[] args) throws Exception { // 実行入口
        // 1) クラス情報から、createOrder メソッドの情報(Method)を取得する
        Method method = AnnotationBasicsMiniDemo.class.getDeclaredMethod("createOrder");

        // 2) 取得したメソッドに付いている @Audit を読み取る
        Audit audit = method.getAnnotation(Audit.class);

        // 3) @Audit の value 属性を表示する（"create-order" が出る）
        System.out.println("Audit value: " + audit.value());
    }
}
