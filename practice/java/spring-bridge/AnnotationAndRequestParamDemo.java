import java.lang.annotation.ElementType; // アノテーションを付けられる対象（型/メソッド/引数など）を表す
import java.lang.annotation.Retention; // アノテーション保持期間を指定するメタアノテーション
import java.lang.annotation.RetentionPolicy; // 保持期間の種類（SOURCE/CLASS/RUNTIME）を表す
import java.lang.annotation.Target; // アノテーション付与対象を指定するメタアノテーション
import java.lang.reflect.Method; // メソッド情報を実行時に扱うための型
import java.lang.reflect.Parameter; // 引数情報を実行時に扱うための型

@Retention(RetentionPolicy.RUNTIME) // 実行時にリフレクションで取得できるようにする
@Target(ElementType.TYPE) // クラスやインターフェースに付与可能
@interface ControllerLike { // @Controller相当の自作アノテーション型を宣言
} // ControllerLike宣言の終わり

@Retention(RetentionPolicy.RUNTIME) // 実行時にも保持する
@Target(ElementType.METHOD) // メソッドに付与可能
@interface GetMappingLike { // @GetMapping相当の自作アノテーション型を宣言
    String value(); // ルート文字列（例: "/hello"）を持つ属性
} // GetMappingLike宣言の終わり

@Retention(RetentionPolicy.RUNTIME) // 実行時にも保持する
@Target(ElementType.PARAMETER) // メソッド引数に付与可能
@interface RequestParamLike { // @RequestParam相当の自作アノテーション型を宣言
    String name(); // パラメータ名を表す必須属性

    boolean required() default true; // 未指定時はtrueになる既定値付き属性
} // RequestParamLike宣言の終わり

@ControllerLike // このクラスがController役であることを示す目印
class GreetingControllerLike { // Controller風のサンプルクラス
    @GetMappingLike("/hello") // "/hello" に対応するハンドラであることを示す
    public String hello(@RequestParamLike(name = "name", required = false) String name) { // 引数nameに属性を付ける
        return name; // 受け取った文字列をそのまま返す（挙動確認用）
    } // helloメソッドの終わり
} // GreetingControllerLikeクラスの終わり

public class AnnotationAndRequestParamDemo { // アノテーション情報を読み取って表示する実行クラス
    public static void main(String[] args) throws Exception { // 実行入口（反射APIを使うので例外をthrows）
        Class<?> clazz = GreetingControllerLike.class; // 対象クラス情報を取得する
        Method method = clazz.getDeclaredMethod("hello", String.class); // helloメソッド情報を取得する
        Parameter param = method.getParameters()[0]; // 1つ目の引数情報を取得する

        GetMappingLike mapping = method.getAnnotation(GetMappingLike.class); // メソッドに付いたGetMappingLikeを取得する
        RequestParamLike requestParam = param.getAnnotation(RequestParamLike.class); // 引数に付いたRequestParamLikeを取得する

        System.out.println("ControllerLike: " + clazz.isAnnotationPresent(ControllerLike.class)); // クラスにControllerLikeが付いているか表示
        System.out.println("GetMappingLike.value: " + mapping.value()); // ルート属性valueを表示
        System.out.println("RequestParamLike.name: " + requestParam.name()); // 引数名属性nameを表示
        System.out.println("RequestParamLike.required: " + requestParam.required()); // 必須属性requiredを表示
    } // mainメソッドの終わり
} // AnnotationAndRequestParamDemoクラスの終わり