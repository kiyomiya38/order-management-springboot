# MySQL-05 ハンズオン: ALTER TABLE / TRUNCATE / DROP

対応参考資料: `J3_03_7_ALTER TABLE.pdf`, `J3_03_8_TRUNCATE.pdf`, `J3_03_9_DROP.pdf`

## 1. この資料のゴール
- `ALTER TABLE` でカラム追加・変更・削除ができる
- `TRUNCATE` と `DELETE` の違いを説明できる
- `DROP TABLE` の影響を理解して安全に実行できる

---

## 2. 事前準備
```bash
mysql -u test_user -p test_db
```

初期データを投入:
```sql
SOURCE ~/order-management-springboot/docs/curriculum/java/mysql-handson/sample-schema-and-data.sql;
```

---

## 3. 先に覚えるポイント
1. `ALTER TABLE ... ADD` でカラム追加
2. `ALTER TABLE ... CHANGE` でカラム名/定義変更
3. `TRUNCATE` は全件削除、`DROP TABLE` はテーブル定義ごと削除

---

## 4. ハンズオン

目的:
- スキーマ変更系DDLを安全に扱う

完了条件:
- `ADD` / `CHANGE` / `DROP COLUMN` / `TRUNCATE` / `DROP TABLE` を確認できる

### Step 0: 変更前定義を確認
```sql
DESCRIBE sample_4_1;
```

### Step 1: カラム追加（ADD）
```sql
ALTER TABLE sample_4_1
ADD color VARCHAR(15) NOT NULL DEFAULT 'WHITE' COMMENT '毛色' AFTER weight;

DESCRIBE sample_4_1;
```

### Step 2: カラム名/定義変更（CHANGE）
```sql
ALTER TABLE sample_4_1
CHANGE color dogs_fur_color VARCHAR(30) NOT NULL DEFAULT 'BROWN' COMMENT '犬の毛色';

DESCRIBE sample_4_1;
```

### Step 3: カラム削除（DROP COLUMN）
```sql
ALTER TABLE sample_4_1
DROP dogs_fur_color;

DESCRIBE sample_4_1;
```

### Step 4: TRUNCATE を確認
```sql
SELECT COUNT(*) AS before_count FROM sample_4_1;
TRUNCATE sample_4_1;
SELECT COUNT(*) AS after_count FROM sample_4_1;
```

### Step 5: DROP TABLE を確認（仕上げ）
```sql
DROP TABLE sample_4_1;
SHOW TABLES LIKE 'sample_4_1';
```

期待結果:
- `sample_4_1` が一覧に出ない

復元:
```sql
SOURCE ~/order-management-springboot/docs/curriculum/java/mysql-handson/sample-schema-and-data.sql;
```

---

## 5. ミニ演習（10分）
1. `sample_4_2` に `memo VARCHAR(100)` を追加してから削除する。
2. `TRUNCATE sample_4_2;` 実行前後の件数を比較する。
3. `DROP TABLE` せず `DELETE FROM` だけで空にした場合との違いを説明する。

---

## 6. つまずきポイント
- `Unknown column` エラー
  -> 変更後のカラム名を使っているか確認
- `DROP TABLE` 後に参照エラー
  -> テーブル復元SQLを再実行する
- 取り返しのつかない削除
  -> 本番ではバックアップ・対象確認を先に行う
