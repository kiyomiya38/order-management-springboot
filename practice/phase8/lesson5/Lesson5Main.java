import java.util.ArrayList;
import java.util.List;

class MappingItem {
    private final String plainJavaConcept;
    private final String springConcept;
    private final String reason;

    MappingItem(String plainJavaConcept, String springConcept, String reason) {
        this.plainJavaConcept = plainJavaConcept;
        this.springConcept = springConcept;
        this.reason = reason;
    }

    String plainJavaConcept() {
        return plainJavaConcept;
    }

    String springConcept() {
        return springConcept;
    }

    String reason() {
        return reason;
    }
}

class SpringMigrationGuide {
    List<MappingItem> mappings() {
        List<MappingItem> list = new ArrayList<>();
        list.add(new MappingItem("Router + Controllerメソッド", "@Controller / @RequestMapping", "URLと処理の対応を宣言で管理する"));
        list.add(new MappingItem("AppConfigでnewして配線", "@Configuration + @Bean / コンポーネントスキャン", "依存解決をコンテナへ委譲する"));
        list.add(new MappingItem("AuthGuard", "Spring Security Filter + 認可設定", "ログイン/権限チェックを共通化する"));
        list.add(new MappingItem("UserCreateValidator", "Bean Validation + @Valid", "入力チェックを定型化する"));
        list.add(new MappingItem("ValidationResult", "BindingResult", "フィールド単位エラーを画面に返す"));
        list.add(new MappingItem("UserRepository(メモリ)", "JPA Repository", "永続化先をDBへ変更する"));
        return list;
    }

    List<String> checklist() {
        List<String> items = new ArrayList<>();
        items.add("1. Domainクラスを最小単位で分離する（User, Roleなど）");
        items.add("2. Serviceに業務ルールを集約し、Controllerからロジックを抜く");
        items.add("3. 画面フォーム用DTOを作り、@Validで検証する");
        items.add("4. RepositoryをDB実装へ差し替える（まずはH2でも可）");
        items.add("5. 認証と認可をSecurityConfigへ寄せる");
        items.add("6. 既存の手動テストケースを自動テストへ置き換える");
        return items;
    }
}

public class Lesson5Main {
    public static void main(String[] args) {
        SpringMigrationGuide guide = new SpringMigrationGuide();

        System.out.println("=== Plain Java -> Spring 対応表 ===");
        for (MappingItem item : guide.mappings()) {
            System.out.println("- " + item.plainJavaConcept() + " -> " + item.springConcept());
            System.out.println("  理由: " + item.reason());
        }

        System.out.println();
        System.out.println("=== 移行チェックリスト ===");
        for (String line : guide.checklist()) {
            System.out.println("- " + line);
        }

        // 最低限の自己検証: 主要項目が揃っているか
        if (guide.mappings().size() < 6) {
            throw new IllegalStateException("対応表が不足しています。");
        }
        if (guide.checklist().size() < 6) {
            throw new IllegalStateException("チェックリストが不足しています。");
        }
    }
}
