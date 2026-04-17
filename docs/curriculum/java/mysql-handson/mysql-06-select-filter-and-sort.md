# MySQL-06 ハンズオン: SELECT / WHERE / NULL / ORDER BY

対応参考資料: `J3_04_1_SELECTの基本.pdf`, `J3_04_2_WHERE句.pdf`, `J3_04_3_NULLの扱い.pdf`, `J3_04_4_ORDER BY.pdf`

## 1. この資料のゴール
- `SELECT` の基本構造を実装できる
- `WHERE` の主要演算子（`IN` / `LIKE` / `BETWEEN`）を使える
- `IS NULL` / `IS NOT NULL` を正しく使える
- `ORDER BY` で昇順・降順を制御できる

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
1. 基本形は `SELECT ... FROM ... WHERE ... ORDER BY ...`
2. `NULL` 比較に `=` は使わず `IS NULL` を使う
3. 条件抽出と並び替えはセットで扱うと実務に近い

---

## 4. ハンズオン

目的:
- 単表検索の基本を一通り実行する

完了条件:
- 条件抽出、NULL判定、並び替えを再現できる

### Step 0: 全件表示
```sql
SELECT *
  FROM sample_4_1;
```

### Step 1: WHERE句で条件抽出
```sql
SELECT id, name, gender, birthday, weight
  FROM sample_4_1
 WHERE gender = 'M'
   AND weight > 6.2;
```

応用:
```sql
SELECT *
  FROM sample_4_1
 WHERE name IN ('MOCO', 'TARO', 'RINRIN');

SELECT *
  FROM sample_4_1
 WHERE birthday LIKE '2013%';

SELECT *
  FROM sample_4_1
 WHERE weight BETWEEN 5.0 AND 8.0;
```

### Step 2: NULL判定を確認
```sql
-- これは失敗例（抽出されない）
SELECT *
  FROM sample_4_2
 WHERE pet_id = NULL;

-- 正しい書き方
SELECT *
  FROM sample_4_2
 WHERE pet_id IS NULL;

SELECT *
  FROM sample_4_2
 WHERE pet_id IS NOT NULL;
```

### Step 3: ORDER BYで並び替え（仕上げ）
```sql
SELECT *
  FROM sample_4_1
 WHERE gender = 'M'
 ORDER BY birthday, weight DESC;

SELECT *
  FROM sample_4_2
 ORDER BY pet_id, home_id;
```

---

## 5. ミニ演習（10分）
1. `sample_4_1` を `name` 昇順で並べる。
2. `gender = 'F'` かつ `weight >= 4.0` を抽出する。
3. `sample_4_2` の `area_id IS NULL` の家庭を抽出する。

---

## 6. つまずきポイント
- `NULL` を `=` で比較して結果が出ない
  -> `IS NULL` / `IS NOT NULL` を使う
- `LIKE` でワイルドカード漏れ
  -> 前方一致は `'2013%'` のように `%` を付ける
- 並び順が逆
  -> `DESC` 指定有無を確認する
