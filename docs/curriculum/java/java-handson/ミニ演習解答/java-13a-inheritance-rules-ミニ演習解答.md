# Java-13A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-13a-inheritance-rules.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3の`Manager`内にある次のコメントを外す:

```java
class Manager extends Worker {
    @Override
    void submitReport() { }
}
```

`Worker.submitReport()`は`final`メソッドなのでコンパイルエラーになる。確認後はこのメソッドを再びコメントアウトし、Step 3がコンパイルできる状態へ戻す。

## レベル2（拡張）解答
レベル1の確認用変更を戻した後、Step 3にある次のコメントを外す:

```java
class DerivedRole extends FixedRole {
}
```

`FixedRole`は`final`クラスなのでコンパイルエラーになる。確認後は`DerivedRole`を再びコメントアウトしてからレベル3へ進む。

## レベル3（実務）解答
レベル1・2の確認用変更を元へ戻した後、`InheritanceRulesDemo.java`を次の全コードへ更新します。

### レベル3完了時の全コード

```java
class Worker {
    // ===== レベル3で変更: 子クラスでオーバーライドできるようfinalを外す =====
    void submitReport() {
        System.out.println("report submitted");
    }
    // ===== レベル3で変更ここまで =====
}

class Manager extends Worker {
    // ===== レベル3で追加: Manager固有の表示へオーバーライドする =====
    @Override
    void submitReport() {
        System.out.println("manager report submitted");
    }
    // ===== レベル3で追加ここまで =====
}

final class FixedRole {
}

// finalクラスを継承できないことはレベル2で確認済み
// class DerivedRole extends FixedRole {
// }

public class InheritanceRulesDemo {
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.submitReport();
    }
}
```

期待出力:

```text
manager report submitted
```
