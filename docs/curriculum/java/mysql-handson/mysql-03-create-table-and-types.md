# MySQL-03 ハンズオン: CREATE TABLEとデータ型

対応参考資料: `J3_03_2_CREATE TABLE.pdf`, `J3_03_4_MySQLのデータ型.pdf`

## 1. この資料のゴール
- `CREATE TABLE` 文でテーブルを作成できる
- `INT` / `VARCHAR` / `CHAR` / `DATE` / `DECIMAL` / `DATETIME` を使い分けできる
- `DEFAULT CURRENT_TIMESTAMP` の挙動を確認できる

---

## 2. 事前準備
```bash
mysql -u test_user -p test_db
```

---

## 3. 先に覚えるポイント
1. 文字列は `VARCHAR(n)`、固定長コードは `CHAR(n)` を選ぶ
2. 金額や小数精度が重要な値は `DECIMAL(p,s)` を使う
3. 監査系の作成日時は `DATETIME DEFAULT CURRENT_TIMESTAMP` が有効

---

## 4. ハンズオン

目的:
- 実務でよく使う型と制約をまとめて実装する

完了条件:
- `sample_4_1` テーブルを作成して `DESCRIBE` で確認できる

### Step 0: テーブルを初期化
```sql
DROP TABLE IF EXISTS sample_4_1;
```

### Step 1: CREATE TABLE を実行
```sql
CREATE TABLE sample_4_1 (
  id                INT          NOT NULL PRIMARY KEY                 COMMENT 'ペットID',
  name              VARCHAR(30)  NOT NULL                             COMMENT '名前',
  gender            CHAR(1)      NOT NULL                             COMMENT '性別（男:M 女:F）',
  birthday          DATE         NOT NULL                             COMMENT '生年月日',
  weight            DECIMAL(4,1)                                      COMMENT '体重',
  regist_timestamp  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP   COMMENT '登録日時'
);
```

### Step 2: 定義を確認
```sql
DESCRIBE sample_4_1;
SHOW CREATE TABLE sample_4_1;
```

期待状態:
- `id` が `PRI` で表示される
- `regist_timestamp` に `DEFAULT CURRENT_TIMESTAMP` が入っている

### Step 3: 型と制約を確認するデータ投入（仕上げ）
```sql
INSERT INTO sample_4_1 (id, name, gender, birthday, weight)
VALUES (1, 'MOCO', 'F', '2014-05-04', 3.5);

SELECT id, name, gender, birthday, weight, regist_timestamp
FROM sample_4_1;
```

学習ポイント:
- `regist_timestamp` を指定しなくても現在時刻が入る
- `DECIMAL(4,1)` は桁数制限がある（入力時に要注意）

---

## 5. ミニ演習（10分）
1. `name` の最大長を 30 から 50 に変更した版を作って比較する。
2. `weight` に `NULL` を入れて登録できることを確認する。
3. `birthday` に不正形式を入れてエラーを確認する。

---

## 6. つまずきポイント
- `You have an error in your SQL syntax`
  -> カンマ区切りと括弧閉じ、末尾セミコロンを確認
- `Data too long` / `Out of range`
  -> `VARCHAR` 長さや `DECIMAL` 桁数が不足していないか確認
- 日付入力エラー
  -> `YYYY-MM-DD` 形式で入力する
