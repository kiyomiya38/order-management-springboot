# MySQL-07 ハンズオン: 集計 / GROUP BY / HAVING

対応参考資料: `J3_04_5_データの集計.pdf`, `J3_04_6_GROUP BY.pdf`, `J3_04_7_HAVING.pdf`

## 1. この資料のゴール
- `COUNT` / `SUM` / `AVG` / `MAX` / `MIN` を使える
- `DISTINCT` と `COUNT(DISTINCT ...)` の違いを説明できる
- `GROUP BY` と `HAVING` を使い分けできる

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
1. 集約関数は `NULL` を除外して計算する（`COUNT(*)` を除く）
2. `WHERE` は行に対する条件、`HAVING` はグループに対する条件
3. `GROUP BY` 後の絞り込みが `HAVING`

---

## 4. ハンズオン

目的:
- 集計クエリの基本パターンを身につける

完了条件:
- 単純集計、グループ集計、HAVING条件を実行できる

### Step 0: 集約関数の基本
```sql
SELECT COUNT(*) AS all_count,
       COUNT(pet_id) AS pet_id_count
  FROM sample_4_2;

SELECT SUM(weight) AS sum_weight,
       AVG(weight) AS avg_weight,
       MAX(weight) AS max_weight,
       MIN(weight) AS min_weight
  FROM sample_4_1;
```

### Step 1: DISTINCT を確認
```sql
SELECT DISTINCT gender
  FROM sample_4_1;

SELECT COUNT(DISTINCT gender) AS gender_kinds
  FROM sample_4_1;
```

### Step 2: GROUP BY で学校別集計
```sql
SELECT school_name AS school_name,
       MAX(score)  AS high_score,
       AVG(score)  AS average_score
  FROM sample_4_4
 WHERE school_name IS NOT NULL
 GROUP BY school_name
 ORDER BY high_score DESC, average_score DESC, school_name;
```

### Step 3: HAVING でグループ条件（仕上げ）
```sql
SELECT school_name AS school_name,
       MAX(score)  AS high_score,
       AVG(score)  AS average_score
  FROM sample_4_4
 WHERE school_name IS NOT NULL
 GROUP BY school_name
HAVING COUNT(*) >= 3
 ORDER BY high_score DESC, average_score DESC, school_name;
```

確認ポイント:
- `COUNT(*) >= 3` は `WHERE` ではなく `HAVING` で書く

---

## 5. ミニ演習（10分）
1. `sample_4_4` を `gender` でグループ化し、人数を集計する。
2. 学校別平均点が 60 以上のグループだけ表示する。
3. `HAVING school_name IS NOT NULL` と `WHERE school_name IS NOT NULL` の結果差を比較する。

---

## 6. つまずきポイント
- 集約関数を `WHERE` に書いてエラー
  -> グループ条件は `HAVING` へ移す
- `GROUP BY` なしで非集約列を選択
  -> 集約列以外は `GROUP BY` 対象に含める
- 並び替えが意図と違う
  -> `ORDER BY` の列順と `DESC` 有無を確認
