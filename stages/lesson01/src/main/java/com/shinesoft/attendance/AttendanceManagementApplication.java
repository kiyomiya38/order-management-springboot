package com.shinesoft.attendance; // パッケージ宣言（配置フォルダと一致させる）

import org.springframework.boot.SpringApplication; // Spring Boot起動クラス
import org.springframework.boot.autoconfigure.SpringBootApplication; // 起点アノテーション

@SpringBootApplication // 設定クラス + コンポーネントスキャン + 自動設定を有効化
public class AttendanceManagementApplication {
    public static void main(String[] args) { // Java実行時の開始地点（エントリポイント）
        SpringApplication.run(AttendanceManagementApplication.class, args); // 起動クラスと引数を渡してアプリ起動
    }
}