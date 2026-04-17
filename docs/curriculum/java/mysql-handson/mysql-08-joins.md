# MySQL-08 ハンズオン: JOIN（内部・外部・3テーブル結合）

対応参考資料: `J3_04_8_内部結合.pdf`, `J3_04_9_外部結合.pdf`, `J3_04_10_３つ以上のテーブル結合.pdf`

## 1. この資料のゴール
- `INNER JOIN` と `OUTER JOIN` の違いを説明できる
- `ON` 句で結合条件を定義できる
- 3テーブル以上の結合クエリを書ける

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
1. `INNER JOIN` は結合条件に一致した行のみ残す
2. `OUTER JOIN` は片側テーブルを残し、欠損側を `NULL` で埋める
3. 可読性のためテーブル別名（`a`, `b`, `c`）を使う

---

## 4. ハンズオン

目的:
- 結合クエリを段階的に作れるようにする

完了条件:
- 内部結合、外部結合、3表結合をそれぞれ実行できる

### Step 0: 結合対象テーブルを確認
```sql
SELECT COUNT(*) FROM sample_4_1;
SELECT COUNT(*) FROM sample_4_2;
SELECT COUNT(*) FROM sample_4_3;
```

### Step 1: 内部結合（INNER JOIN）
```sql
SELECT b.home_id, b.home_name, a.name
  FROM sample_4_1 a
 INNER JOIN sample_4_2 b ON b.pet_id = a.id
 ORDER BY b.home_id;
```

期待結果:
- `pet_id` が `NULL` の家庭は出力されない

### Step 2: 外部結合（RIGHT OUTER JOIN）
```sql
SELECT b.home_id, b.home_name, a.name
  FROM sample_4_1 a
 RIGHT OUTER JOIN sample_4_2 b ON b.pet_id = a.id
 ORDER BY b.home_id;
```

確認ポイント:
- ペット未登録家庭が `a.name = NULL` で残る

### Step 3: 3テーブル結合（仕上げ）
```sql
SELECT b.home_id, b.home_name, a.name, c.area_name
  FROM (sample_4_1 a RIGHT OUTER JOIN sample_4_2 b ON b.pet_id = a.id)
  LEFT OUTER JOIN sample_4_3 c ON b.area_id = c.area_id
 ORDER BY b.home_id;
```

学習ポイント:
- 結合順序で結果が変わるため、括弧で意図を明示する

---

## 5. ミニ演習（10分）
1. Step 1 を `LEFT OUTER JOIN` に変え、差分を比較する。
2. `WHERE c.area_name IS NULL` を追加し、地域未設定家庭のみ抽出する。
3. `home_name` で昇順ソートする版を作る。

---

## 6. つまずきポイント
- 別名が重複して参照エラー
  -> 各テーブルに一意な別名を付ける
- JOIN 条件漏れで件数が爆発
  -> `ON` 句のキー対応（`pet_id = id` 等）を確認
- OUTER JOINなのに件数が増えない
  -> `WHERE` で `NULL` 行を落としていないか確認
