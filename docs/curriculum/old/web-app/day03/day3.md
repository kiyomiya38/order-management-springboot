# Day3: クエスト進捗CLIを作る（Java基礎）

## ロードマップ接続
- 対象: `java-web-1week-beginner-blueprint.md` の Day3
- このDayで行うこと: Day2の状態管理をJavaコンソールアプリへ移植する
- 到達点: Javaで `追加 / 一覧 / 完了 / 削除` が動くCLIアプリを完成させる

## Day2とのつながり
- Day2の `habits` 配列 -> Day3の `List<Quest>`
- Day2の `render()` -> Day3の `printQuests()`
- Day2のイベント処理 -> Day3のメニュー入力（`Scanner`）

---

## 1. 作業フォルダ
```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/day03-quest-cli
cd ~/order-management-springboot/practice/day03-quest-cli
```

---

## 2. Day3で使うファイル（自分で新規作成）
- `Quest.java`
  - 1件分のデータ（id, タイトル, 完了状態, 報酬XP）
- `QuestService.java`
  - 一覧の状態管理（追加、完了、削除、表示）
- `App.java`
  - メニュー表示と入力受付（アプリの開始地点）

---

## 3. Step1: `Quest.java` を作る（クラスの基本）

### このStepで学ぶ文法
- `class`
- フィールド（変数）
- コンストラクタ
- getter

### 3-1. `Quest.java` を作成
作成ファイル:
- `Quest.java`

```java
public class Quest {
    private final int id;
    private final String title;
    private final int xpReward;
    private boolean done;

    public Quest(int id, String title, int xpReward) {
        this.id = id;
        this.title = title;
        this.xpReward = xpReward;
        this.done = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getXpReward() {
        return xpReward;
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        this.done = true;
    }
}
```

---

## 4. Step2: `QuestService.java` を作る（状態管理）

### このStepで学ぶ文法
- `List` と `ArrayList`
- `for-each`
- `if` 条件分岐
- メソッド分割

### 4-1. `QuestService.java` を作成
作成ファイル:
- `QuestService.java`

```java
import java.util.ArrayList;
import java.util.List;

public class QuestService {
    private final List<Quest> quests = new ArrayList<>();
    private int nextId = 1;
    private int totalXp = 0;

    public void addQuest(String title) {
        int reward = 10;
        quests.add(new Quest(nextId, title, reward));
        nextId++;
    }

    public boolean completeQuest(int id) {
        Quest quest = findById(id);
        if (quest == null) {
            return false;
        }
        if (quest.isDone()) {
            return false;
        }

        quest.markDone();
        totalXp += quest.getXpReward();
        return true;
    }

    public boolean removeQuest(int id) {
        Quest quest = findById(id);
        if (quest == null) {
            return false;
        }
        return quests.remove(quest);
    }

    public void printQuests() {
        System.out.println();
        System.out.println("=== クエスト一覧 ===");

        if (quests.isEmpty()) {
            System.out.println("まだクエストがありません。");
        } else {
            for (Quest quest : quests) {
                String status = quest.isDone() ? "完了" : "未完了";
                System.out.println(
                        "[" + quest.getId() + "] "
                                + quest.getTitle()
                                + " | 状態: " + status
                                + " | 報酬XP: " + quest.getXpReward()
                );
            }
        }

        System.out.println("累計XP: " + totalXp);
        System.out.println("現在レベル: Lv." + getLevel());
        System.out.println();
    }

    private Quest findById(int id) {
        for (Quest quest : quests) {
            if (quest.getId() == id) {
                return quest;
            }
        }
        return null;
    }

    private int getLevel() {
        return (totalXp / 30) + 1;
    }
}
```

---

## 5. Step3: `App.java` を作る（入力とメニュー）

### このStepで学ぶ文法
- `main` メソッド
- `while` ループ
- `switch`
- `Scanner`
- `try-catch`

### 5-1. `App.java` を作成
作成ファイル:
- `App.java`

```java
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        QuestService service = new QuestService();
        boolean running = true;

        System.out.println("クエスト進捗CLIへようこそ！");

        while (running) {
            printMenu();
            String command = scanner.nextLine().trim();

            switch (command) {
                case "1":
                    handleAdd(scanner, service);
                    break;
                case "2":
                    service.printQuests();
                    break;
                case "3":
                    handleComplete(scanner, service);
                    break;
                case "4":
                    handleRemove(scanner, service);
                    break;
                case "5":
                    running = false;
                    System.out.println("終了します。おつかれさまでした！");
                    break;
                default:
                    System.out.println("1〜5の番号を入力してください。");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("==== メニュー ====");
        System.out.println("1: クエスト追加");
        System.out.println("2: クエスト一覧");
        System.out.println("3: クエスト完了");
        System.out.println("4: クエスト削除");
        System.out.println("5: 終了");
        System.out.print("選択 > ");
    }

    private static void handleAdd(Scanner scanner, QuestService service) {
        System.out.print("クエスト名を入力 > ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("空文字は登録できません。");
            return;
        }

        service.addQuest(title);
        System.out.println("クエストを追加しました。");
    }

    private static void handleComplete(Scanner scanner, QuestService service) {
        System.out.print("完了にするID > ");
        int id = readInt(scanner);
        if (id < 0) {
            return;
        }

        boolean updated = service.completeQuest(id);
        if (updated) {
            System.out.println("クエストを完了にしました。");
        } else {
            System.out.println("対象が見つからないか、すでに完了済みです。");
        }
    }

    private static void handleRemove(Scanner scanner, QuestService service) {
        System.out.print("削除するID > ");
        int id = readInt(scanner);
        if (id < 0) {
            return;
        }

        boolean removed = service.removeQuest(id);
        if (removed) {
            System.out.println("クエストを削除しました。");
        } else {
            System.out.println("対象IDが見つかりません。");
        }
    }

    private static int readInt(Scanner scanner) {
        String raw = scanner.nextLine().trim();
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            System.out.println("数値を入力してください。");
            return -1;
        }
    }
}
```

---

## 6. 実行して動作確認

```bash
javac -encoding UTF-8 App.java Quest.java QuestService.java
java App
```

確認項目:
1. `1` でクエストを追加できる
2. `2` で一覧表示できる
3. `3` で完了にできる
4. `4` で削除できる
5. `5` で終了できる

---

## 7. Day3コードの説明（入力・処理・出力）

1. 入力
- メニュー番号（1〜5）
- クエスト名
- 対象ID

2. 処理
- `QuestService` が `List<Quest>` を更新
- 完了時に `totalXp` を加算
- 画面表示用の文字列を組み立て

3. 出力
- コンソールに一覧、完了状態、レベル表示

---

## 8. よくあるエラー

- `cannot find symbol`（クラスが見つからない）
  - ファイル名とクラス名が一致しているか確認（`QuestService.java` など）

- `Error: Could not find or load main class App`
  - `javac` 実行後に `java App` を同じフォルダで実行しているか確認

- 日本語が文字化けする
  - `javac -encoding UTF-8 ...` でコンパイルしているか確認

---

## 9. 最終確認（Day3完了条件）

- 3ファイルを自分で作成できた
- 追加 / 一覧 / 完了 / 削除 が動く
- `List<Quest>` と `QuestService` の役割を説明できる
- Day2の状態管理との対応関係を説明できる

## 10. Day4への引き継ぎ

- Day4では、Day3のJavaロジックをWeb化して、Servletで「ブラウザ入力 -> Java処理 -> 画面表示」を体験する

