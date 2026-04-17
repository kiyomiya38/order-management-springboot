# MySQL-02 ハンズオン: MySQLインストールと初期設定（Windows）

対応参考資料: `J3_02_MySQLのインストール（Win）.pdf`, `J3_02_MySQLの設定（貼り付け用）.txt`

## 1. この資料のゴール
- MySQL クライアント起動とログイン確認ができる
- 学習用ユーザー / DB を作成できる
- 権限付与と認証プラグイン確認ができる

---

## 2. 事前準備
- Windows 環境で MySQL 8 系をインストール済み
- root ユーザーのパスワードを把握している

確認:
```bash
mysql --version
```

---

## 3. 先に覚えるポイント
1. 学習用途では root 常用を避け、専用ユーザーを作る
2. `GRANT` で DB ごとの権限を付与する
3. `SHOW GRANTS` で付与内容を必ず検証する

---

## 4. ハンズオン

目的:
- 学習で使う最小構成の MySQL 環境を作る

完了条件:
- `test_user` / `test_db` でログイン・操作できる

### Step 0: root でログイン
```bash
mysql -u root -p
```

### Step 1: 学習用ユーザーを作成
```sql
CREATE USER test_user@localhost IDENTIFIED BY 'test_pass';
SELECT Host, User FROM mysql.user;
```

期待結果:
- `test_user` が一覧に表示される

### Step 2: 学習用DBを作成
```sql
CREATE DATABASE test_db DEFAULT CHARACTER SET utf8;
SHOW DATABASES;
```

期待結果:
- `test_db` が一覧に表示される

### Step 3: 権限を付与して確認
```sql
GRANT ALL PRIVILEGES ON test_db.* TO test_user@localhost;
SHOW GRANTS FOR test_user@localhost;
```

期待結果:
- `test_db` への権限付与が確認できる

### Step 4: 認証プラグインを確認・必要なら変更
```sql
SELECT User, Plugin FROM mysql.user;

-- 必要な場合のみ実行
ALTER USER test_user@localhost IDENTIFIED WITH mysql_native_password BY 'test_pass';
SELECT User, Plugin FROM mysql.user;
```

### Step 5: test_user で接続確認（仕上げ）
```bash
mysql -u test_user -p test_db
```

接続後に確認:
```sql
SELECT DATABASE();
QUIT;
```

---

## 5. ミニ演習（10分）
1. `practice_db` を追加で作成し、`test_user` 権限を付与する。
2. `SHOW GRANTS FOR test_user@localhost;` の結果を読み、どの DB に権限があるか説明する。
3. `QUIT;` で終了後、再ログインできることを確認する。

---

## 6. つまずきポイント
- `Access denied for user`
  -> ユーザー名 / パスワード / ホスト（`localhost`）を確認
- `CREATE USER` で既存ユーザーエラー
  -> 同名ユーザーがある場合は `DROP USER` か名前変更で対応
- `Unknown database 'test_db'`
  -> DB 作成とスペルを確認
