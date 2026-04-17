# Java-12A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-12a-access-modifiers.md`

## ミニ演習解答
1. `loginFailures` を `private` で追加:

```java
public class Account {
    private int loginFailures = 0;

    public void recordFailure() {
        loginFailures++;
    }

    public void resetFailures() {
        loginFailures = 0;
    }

    public int getLoginFailures() {
        return loginFailures;
    }
}
```

2. 非継承クラスから `protected` へ直接アクセス:
- `base.points` でコンパイルエラー（他パッケージ通常クラスから不可）

3. `InternalRule` を `public` 化:

```java
package model;

public class InternalRule {
    public static String value() {
        return "internal-rule";
    }
}
```

`app` 側:

```java
import model.InternalRule;
System.out.println(InternalRule.value());
```
