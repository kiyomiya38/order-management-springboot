# Java-13 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-13-inheritance.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
`Manager`クラスより後、`InheritanceDemo`クラスより前へ次のクラスを追加する。

```java
class PartTimeEmployee extends Employee {
}
```

`main(...)`の既存処理より後へ、生成・名前設定・表示を追加する。

```java
PartTimeEmployee partTime = new PartTimeEmployee();
partTime.name = "Sato";
partTime.printProfile();
```

期待出力は`社員: Sato`。

## レベル2（拡張）解答
レベル1の`PartTimeEmployee`を次の内容へ更新する。
```java
class PartTimeEmployee extends Employee {
    @Override
    String roleLabel() {
        return "アルバイト";
    }
}
```

期待出力は`アルバイト: Sato`。

レベル1で追加した次の呼び出しコードは変更せず、そのまま使用する。

```java
PartTimeEmployee partTime = new PartTimeEmployee();
partTime.name = "Sato";
partTime.printProfile();
```

## レベル3（実務）解答
レベル2のクラス定義を残し、`main(...)`の生成・表示処理を次の内容へ更新する。
```java
Employee employee = new Employee();
employee.name = "Tanaka";
Manager manager = new Manager();
manager.name = "Suzuki";
PartTimeEmployee partTime = new PartTimeEmployee();
partTime.name = "Sato";

employee.printProfile();
manager.printProfile();
partTime.printProfile();
```
