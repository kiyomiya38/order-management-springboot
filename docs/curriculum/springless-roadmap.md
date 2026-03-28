# Springなしで同等アプリを作る学習ロードマップ（完成UI先行版）

## 0. このロードマップの目的
- `C:/Users/Shinesoft/order-management-springboot/src` の最終アプリ機能を、Spring Bootなしで段階的に再実装する。
- 文法を先に網羅せず、必要になった瞬間に最小限だけ学ぶ。
- 最初に完成版UI（見た目のみ）を作り、その同じコードへ機能を追加していく。
- 各Lessonの合格判定はブラウザ操作シナリオで行う（コンソールは中間確認）。

---

## 1. 開発スタイル（固定）
1. 作業フォルダは固定: `~/order-management-springboot/practice/springless-final-web`
2. `lesson1` で完成版UIを先に作る（画面見た目のみ）
3. `lesson2` 以降は同じコードを編集し、1機能ずつ有効化する
4. 画面を壊さずに機能を追加する（常に `http://localhost:8080/` は表示可能）

---

## 2. 各Lessonの進め方
1. `src` の対応コードを先に読む（15〜30分）
2. 追加する機能を1つ決める
3. 必要な文法だけ学ぶ
4. 同じコードに追記する
5. ブラウザで1シナリオ確認する
6. `src` との差分を3行でメモする

---

## 3. 最終ゴール（src同等）
1. ログイン/ログアウト（一般ユーザー・管理者）
2. 一般ユーザーの打刻（出勤/退勤）と当日状態表示
3. 一般ユーザーの勤怠一覧表示
4. 管理者のユーザー管理（一覧/作成/編集/削除）
5. 管理者の勤怠管理（一覧/編集）
6. 入力バリデーション（必須・形式・業務ルール）
7. 永続化（DB接続、再起動後もデータ保持）
8. テストで主要業務ルールを固定

---

## 4. 学習ロードマップ（全8Phase）

## Phase 1: 完成版UIを先に作る（見た目のみ）
### 先に読む `src`
- `web/*Controller`
- `templates/index.html`, `templates/login.html`, `templates/attendances.html`, `templates/users.html`, `templates/admin-attendances.html`

### 追加する機能
- `HttpServer` を起動し、以下の画面を静的HTMLで表示
- `/`（トップ）, `/login`, `/attendances`, `/admin/users`, `/admin/attendances`
- 画面間リンクをつなぐ（処理はまだダミー）

### このPhaseで使う最小文法
- クラス、メソッド、文字列、条件分岐

### 動作確認
- 各URLがブラウザで表示される
- どの画面からもトップへ戻れる

---

## Phase 2: 打刻機能を有効化（トップ画面）
### 先に読む `src`
- `service/AttendanceService`
- `domain/Attendance`, `domain/AttendanceStatus`
- `exception/BusinessException`

### 追加する機能
- 出勤/退勤の状態遷移ルールを実装
- トップ画面のボタンを実処理へ接続
- 業務違反時のエラーメッセージ表示

### このPhaseで使う最小文法
- `if / else`, `throw`, `try-catch`, `enum`

### 動作確認
- 未出勤 -> 出勤 -> 退勤 が通る
- 二重出勤、未出勤退勤でエラー表示

---

## Phase 3: 勤怠一覧画面を有効化
### 先に読む `src`
- `web/AttendanceController`
- `templates/attendances.html`

### 追加する機能
- 一覧データをメモリから取得して表示
- PRG（POST-Redirect-GET）を導入

### このPhaseで使う最小文法
- `List`, for-each, `Optional`

### 動作確認
- 打刻後に一覧へ反映される
- リロードで二重送信が起きない

---

## Phase 4: 手動DIと層分離
### 先に読む `src`
- `config/*`
- `web/*Controller`, `service/*Service`, `repository/*Repository`

### 追加する機能
- `AppConfig` で依存を集中組み立て
- Controller / Service / Repository の責務分離

### このPhaseで使う最小文法
- `interface`, `implements`, `private final`

### 動作確認
- 既存画面が壊れない
- 差し替え変更が `AppConfig` 中心になる

---

## Phase 5: ログイン/ログアウトと認可
### 先に読む `src`
- `config/SecurityConfig`
- `web/AuthController`

### 追加する機能
- `/login`, `/logout` の実装
- Cookieセッション管理
- `ADMIN` / `USER` のURLアクセス制御

### このPhaseで使う最小文法
- `Map`, UUID, 文字列処理

### 動作確認
- 未ログインは `/login` へ誘導される
- 権限外URLが拒否される

---

## Phase 6: 管理者画面（ユーザー管理・勤怠管理）を有効化
### 先に読む `src`
- `web/UserController`
- `web/AdminAttendanceController`
- `web/form/*`

### 追加する機能
- ユーザー管理（一覧/作成/編集/削除）
- 管理者勤怠編集
- 入力バリデーション

### このPhaseで使う最小文法
- 文字列バリデーション、`switch` 式、`Optional#orElseThrow`

### 動作確認
- 管理画面の主要操作が実行できる
- 不正入力時に画面へ理由が表示される

---

## Phase 7: テスト導入
### 先に読む `src`
- `test/.../AttendanceServiceTest`
- `service/AttendanceService`, `service/UserService`

### 追加する機能
- 業務ルールの正常系/異常系テスト
- 回帰を防ぐ最小テストスイート

### このPhaseで使う最小文法
- JUnit, `assertEquals`, `assertThrows`

### 動作確認
- テストが通る
- ルール破壊時にテストが落ちる

---

## Phase 8: 永続化 + 最終リファクタリング + Spring対応づけ
### 先に読む `src`
- `application.yml`
- `repository/*`
- `domain/*`

### 追加する機能
- メモリ保存を JDBC 実装へ差し替え
- DB接続で永続化
- 手動実装とSpring実装の対応表を完成

### このPhaseで使う最小文法
- JDBC, `try-with-resources`, SQLバインド

### 動作確認
- 再起動後もデータが残る
- Spring版への置き換えポイントを説明できる

---

## 5. Phase完了の判定基準
1. ブラウザでそのPhaseの主要シナリオが動く
2. エラー時の挙動を説明できる
3. `src` との差分を説明できる

---

## 6. この構成で得られること
- 最初に全体画面が見えるため、機能追加の意味が追いやすい
- 同じコードを育てるため、改善の履歴が分かりやすい
- Spring版との対応関係を「機能単位」で説明しやすい
