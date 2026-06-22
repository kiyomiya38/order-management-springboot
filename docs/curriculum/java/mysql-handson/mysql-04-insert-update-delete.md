# MySQL-04 ハンズオン: INSERT / UPDATE / DELETE

対応参考資料: `J3_03_3_INSERT.pdf`, `J3_03_5_UPDATE.pdf`, `J3_03_6_DELETE.pdf`

## 1. この資料のゴール
- `INSERT` / `UPDATE` / `DELETE` の基本文を実装できる
- `WHERE` なし更新・削除の危険性を理解できる
- 変更前後を `SELECT` で検証できる

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
1. `INSERT` は列名を明示して実行する
2. `UPDATE` / `DELETE` は `WHERE` で対象を限定する
3. 実行前後に `SELECT` で差分を確認する

---

## 4. ハンズオン

目的:
- DML の基本操作を安全に実行する

完了条件:
- 追加・更新・削除をそれぞれ1回以上実行し、結果を確認できる

### Step 0: 初期状態を確認
```sql
SELECT id, name, weight
FROM sample_4_1
ORDER BY id;
```

### Step 1: INSERT でレコード追加
```sql
INSERT INTO sample_4_1 (id, name, gender, birthday, weight)
VALUES (6, 'POPO', 'F', '2016-02-01', 4.1);

SELECT id, name, weight
FROM sample_4_1
WHERE id = 6;
```

### Step 2: UPDATE で体重を更新
```sql
UPDATE sample_4_1
   SET weight = 4.3
 WHERE id = 6;

SELECT id, name, weight
FROM sample_4_1
WHERE id = 6;
```

### Step 3: DELETE でレコード削除（仕上げ）
```sql
DELETE FROM sample_4_1
 WHERE id = 6;

SELECT id, name, weight
FROM sample_4_1
WHERE id = 6;
```

期待状態:
- 最後の `SELECT` は 0 件

---

## 5. ミニ演習（10分）
1. `id = 5` の `weight` を 10.9 から 11.2 に更新する。
2. `gender = 'M'` の件数を `SELECT COUNT(*)` で確認してから、`DELETE` は実行せず安全確認だけ行う。
3. `INSERT` 時に `NOT NULL` カラムを省略してエラーを確認する。

---

## 6. つまずきポイント
- `Duplicate entry` エラー
  -> 主キー `id` が既存値と重複
- `UPDATE` 後に件数が変わらない
  -> `WHERE` 条件が一致しているか確認
- 意図しない大量更新/削除
  -> 本番運用では `SELECT` で対象確認後に DML を実行
