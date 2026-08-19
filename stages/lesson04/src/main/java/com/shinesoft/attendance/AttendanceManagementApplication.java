// このクラスが属するパッケージ（フォルダ構成と一致させる）
package com.shinesoft.attendance;

// Spring Boot起動用クラス
import org.springframework.boot.SpringApplication;
// このクラスをSpring Bootの起点として扱う
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 設定読み込み・コンポーネントスキャン・自動設定を有効化
@SpringBootApplication
public class AttendanceManagementApplication {
    // Java実行時の開始地点
    public static void main(String[] args) {
        // Spring Bootアプリを起動する
        SpringApplication.run(AttendanceManagementApplication.class, args);
    }
}