# MySQL-01 ハンズオン: データベースとRDBMSの基礎

対応参考資料: `J3_01_データベースとは.pdf`

## 1. この資料のゴール
- データベース / RDBMS の違いを説明できる
- テーブル / レコード / カラムの用語を説明できる
- 主キー（`PRIMARY KEY`）と `NOT NULL` の意味を説明できる

---

## 2. 事前準備
```bash
mysql --version
```

期待状態:
- `mysql  Ver 8.x ...` のようにバージョンが表示される

---

## 3. 先に覚えるポイント
1. RDBMS は「表（テーブル）」でデータを管理する仕組み
2. テーブルはカラム定義と複数レコードで構成される
3. 主キーは「重複不可 + NULL不可」の識別子
4. `NOT NULL` は「空データ禁止」の制約

---

## 4. ハンズオン

目的:
- 主要用語と制約を SQL 実行で確認する

完了条件:
- 主キーと `NOT NULL` の制約違反を実際に確認できる

作業DB: `test_db`（未作成なら MySQL-02 で作成）

### Step 0: MySQLへログイン
```bash
mysql -u test_user -p test_db
```

### Step 1: 用語確認用の最小テーブルを作る
```sql
DROP TABLE IF EXISTS basics_users;

CREATE TABLE basics_users (
  user_id    INT         NOT NULL PRIMARY KEY,
  user_name  VARCHAR(30) NOT NULL,
  age        INT
);
```

確認ポイント:
- テーブル名: `basics_users`
- カラム: `user_id`, `user_name`, `age`
- レコードはまだ 0 件

### Step 2: 正常データを登録してテーブル構造を確認
```sql
INSERT INTO basics_users (user_id, user_name, age)
VALUES (1, 'Tanaka', 25);

SELECT * FROM basics_users;
```

期待状態:
- 1件表示される

### Step 3: 制約違反を確認（仕上げ）
以下は順に実行し、エラーメッセージを確認する。

```sql
-- 主キー重複（失敗）
INSERT INTO basics_users (user_id, user_name, age)
VALUES (1, 'Suzuki', 30);

-- NOT NULL違反（失敗）
INSERT INTO basics_users (user_id, user_name, age)
VALUES (2, NULL, 20);
```

学習ポイント:
- 主キー重複は受け付けられない
- `NOT NULL` のカラムに `NULL` は入れられない

---

## 5. ミニ演習（10分）
1. `email VARCHAR(100) NOT NULL` を追加したテーブルを作る。
2. `age` を `NULL` で登録し、成功することを確認する。
3. `SELECT user_id, user_name FROM basics_users;` で必要列のみ抽出する。

---

## 6. つまずきポイント
- `ERROR 1049 (42000): Unknown database`
  -> `test_db` が未作成。MySQL-02 の作成手順を先に実施
- `PRIMARY KEY` 制約エラー
  -> 既存 `user_id` と重複していないか確認
- `cannot be null` エラー
  -> `NOT NULL` カラムに値を設定する
