# Springless 完成形リファレンス（最短で動く版）

## 1. 目的
先に完成形を確認したい、という要望に合わせた参照実装です。  
Spring Bootなしで、以下の機能を1つのWebアプリとして動かせます。

- ログイン / ログアウト
- 一般ユーザーの打刻（出勤 / 退勤）
- 管理者のユーザー管理（一覧 / 新規作成）
- 管理者の当日勤怠一覧
- 入力バリデーション（必須、形式、文字数、重複）
- 未ログイン保護 / 管理者権限チェック

## 2. 実装場所
- `C:/Users/Shinesoft/order-management-springboot/practice/springless-final`

主要ファイル:
- `DomainModels.java`
- `Repositories.java`
- `Services.java`
- `WebUtil.java`
- `Controllers.java`
- `SpringlessApp.java`

## 3. 起動手順（Maven不要）
```bash
cd ~/order-management-springboot/practice/springless-final
javac *.java
java SpringlessApp
```

アクセス:
- `http://localhost:8080/login`

初期ユーザー:
- `admin / adminpass`（ADMIN）
- `user / userpass`（USER）

## 4. 補足
- この完成形は「最短で動く」ことを優先し、保存先はメモリ（再起動で消える）です。
- Mavenは使っていません。  
  Spring Bootを使わなくても、必要なら Maven をビルド/テスト用に使うことは可能ですが、この完成版では採用していません。
