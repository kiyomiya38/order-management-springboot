# Java-13A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-13a-inheritance-rules.md`

## ミニ演習解答
1. `GrandParent -> Parent -> Child` の3階層:

```java
class GrandParent {
    GrandParent() { System.out.println("GrandParent()"); }
}

class Parent extends GrandParent {
    Parent() { System.out.println("Parent()"); }
}

class Child extends Parent {
    Child() { System.out.println("Child()"); }
}
```

`new Child()` の出力順: `GrandParent -> Parent -> Child`

2. `Parent` に引数なしコンストラクタ追加:

```java
class Parent extends GrandParent {
    Parent() { System.out.println("Parent()"); }
    Parent(String name) { System.out.println("Parent name=" + name); }
}
```

`Child` 側で `super(...)` 省略時は暗黙 `super()` が呼ばれる。

3. `final` メソッドを通常メソッドへ変更:

```java
class Worker {
    void submitReport() { System.out.println("base"); } // final を外す
}

class Manager extends Worker {
    @Override
    void submitReport() { System.out.println("override"); }
}
```
