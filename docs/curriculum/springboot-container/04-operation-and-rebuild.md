# 04 アプリを操作し、変更を再ビルドする

[前へ：コンテナ化ハンズオン](./03-container-handson.md) ｜ [次へ：Docker Hubへのpush](./05-dockerhub-push.md)

この章では、起動した勤怠管理アプリの機能を確認します。その後、データがvolumeへ残ることを体験し、Java側の表示メッセージを1か所変更してコンテナへ反映します。

作業場所は引き続き次のフォルダです。

```bash
cd ~/order-management-springboot/stages/springboot-container
```

## 1. コンテナの状態を確認する

```bash
docker compose ps
```

`app`が`Up`、`db`が`Up (healthy)`になっていれば、そのまま進みます。停止している場合は起動します。

```bash
docker compose up -d
```

ブラウザで次を開きます。

```text
http://localhost:8081/login
```

この章で使用する初期ユーザーは次の2人です。

| ユーザー | 権限 | パスワード |
| --- | --- | --- |
| `admin` | 管理者 | `.env`の`APP_SEED_ADMIN_PASSWORD` |
| `user1` | 一般ユーザー | `.env`の`APP_SEED_USER_PASSWORD` |

教材どおりの`.env`なら、パスワードはそれぞれ`TrainingAdminPass2026`と`TrainingUserPass2026`です。

## 2. 完成版の機能を確認する

### 2-1. 一般ユーザーの画面

まず`user1`でログインし、次を確認します。

1. 今日の日付と勤怠状態が表示される
2. 「出勤」ボタンを押すと出勤時刻が表示される
3. 「退勤」ボタンを押すと退勤時刻が表示される
4. 「勤怠一覧」から自分の記録を確認できる
5. 一般ユーザーには管理者用メニューが表示されない

この章の後半でも出勤操作を行うため、ここで記録したデータはいったん初期化します。初期化手順は後ほど明示します。

### 2-2. 管理者の画面

ログアウトし、`admin`でログインします。最初に、登録・編集・削除を次の専用ユーザーで確認します。

1. 「アカウント管理」を開く
2. ユーザー名`edit-delete-user`、パスワード`EditDeletePass2026`、権限`ROLE_USER`で登録する
3. 登録したユーザー名を`edited-user`へ変更する
4. `edited-user`を削除する
5. 一覧から削除されたことを確認する

続けて「勤怠管理」を開き、利用者の勤怠を検索・編集できる画面が用意されていることを確認します。

次に、volume確認用として次の一般ユーザーを作成してください。このユーザーは、後の確認が終わるまで編集・削除しません。

| 項目 | 入力例 |
| --- | --- |
| ユーザー名 | `volume-user` |
| パスワード | `VolumeUserPass2026` |
| 権限 | `ROLE_USER` |

### 2-3. APIが動くことを確認する

この完成版には、画面だけでなくAPIもあります。Git Bashで`.env`から管理者の研修用パスワードだけを読み取り、管理者としてユーザー一覧を取得します。

```bash
ADMIN_PASSWORD=$(sed -n 's/^APP_SEED_ADMIN_PASSWORD=//p' .env | tr -d '\r')
curl -u "admin:${ADMIN_PASSWORD}" http://localhost:8081/api/users
unset ADMIN_PASSWORD
```

JSON形式のユーザー一覧に`admin`、`user1`、`volume-user`が含まれていれば成功です。

`tr -d '\r'`は、Windows形式の改行が付いていても末尾の余分な文字を除くための指定です。確認後は`unset`で変数を削除しています。ターミナルの出力を共有するときは、秘密情報が含まれていないことを確認してください。

## 3. volumeによるデータ保持を確認する

現在、`volume-user`はMariaDBの`db_data` volumeへ保存されています。

まずコンテナを停止・削除します。

```bash
docker compose down
```

通常の`down`では、コンテナとnetworkは削除されますが、名前付きvolumeは残ります。再び起動します。

```bash
docker compose up -d
docker compose ps
```

`db`がhealthy、`app`がUpになったら、ブラウザから`admin`でログインします。「アカウント管理」に`volume-user`が残っていれば、volumeによるデータ保持を確認できました。

詳しくは[Docker公式: Volumes](https://docs.docker.com/engine/storage/volumes/)を参照してください。

```text
コンテナを削除
  └─ db_data volumeは残る
       └─ 新しいdbコンテナへ再接続
            └─ ユーザーや勤怠データが再び見える
```

## 4. 研修データを初期状態へ戻す

次のJava変更を確実に確認するため、`user1`がまだ出勤していない状態へ戻します。

> **警告:** 次の`docker compose down -v`は、ユーザーや勤怠記録を保存したDB volumeを削除します。削除したデータは元に戻せません。この研修用データを消してよいことを確認してから実行してください。仕事で使用している環境では実行しないでください。

削除対象がこの教材のCompose構成であることを確認します。

```bash
pwd
docker compose ps
```

`pwd`の末尾が`/stages/springboot-container`であることを確認してから実行します。

```bash
docker compose down -v
```

`-v`の意味や`down`の動作は[Docker公式: docker compose down](https://docs.docker.com/reference/cli/docker/compose/down/)で確認できます。

空のvolumeを新しく作り、起動し直します。

```bash
docker compose up -d
docker compose ps
```

MariaDBの初期化とSpring Bootの起動が終わるまで待ちます。`admin`でログインし、次を確認します。

- `volume-user`が存在しない
- 初期ユーザーの`admin`と`user1`が再作成されている
- `user1`の今日の勤怠が未出勤である

これでJava側のメッセージ変更を確認できる新しい状態になりました。

## 5. Java側の表示メッセージを変更する

変更するファイルは次の1ファイルです。

```text
src/main/java/com/shinesoft/attendance/web/HomeController.java
```

VS Codeで開きます。

```bash
code src/main/java/com/shinesoft/attendance/web/HomeController.java
```

`clockIn`メソッド内にある、次の行を探します。

```java
redirectAttributes.addFlashAttribute("message", "出勤しました");
```

この1行だけを、次のように変更します。

```java
redirectAttributes.addFlashAttribute("message", "本日の出勤を受け付けました");
```

変更後の`clockIn`メソッドは、次の形になります。メソッド全体を追加するのではなく、既存メソッド内のメッセージ1行だけを変更してください。

```java
@PostMapping("/clock-in")
public String clockIn(RedirectAttributes redirectAttributes, Principal principal) {
    var user = userService.getByUsername(principal.getName());
    try {
        service.clockIn(user.getId());
        redirectAttributes.addFlashAttribute("message", "本日の出勤を受け付けました");
    } catch (BusinessException ex) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
    }
    return "redirect:/";
}
```

保存後、変更した文字列がファイル内にあることを確認します。

```bash
grep -n "本日の出勤を受け付けました" src/main/java/com/shinesoft/attendance/web/HomeController.java
```

1行表示されれば保存できています。

## 6. imageを再ビルドして変更を反映する

ソースコードを変更しても、すでに動いているコンテナの中身は自動では変わりません。Dockerfileから新しいimageを作り、そのimageで`app`コンテナを作り直します。

```bash
docker compose up -d --build
```

このコマンドでは、主に次が行われます。

```text
変更後のJavaソース
  → Mavenで新しいJARを作る
  → 新しいapp imageを作る
  → appコンテナを作り直す

db_data volume
  → 削除しないため、そのまま残る
```

起動状態とログを確認します。

```bash
docker compose ps
docker compose logs --tail=100 app
```

`app`がUp、`db`がhealthyになれば反映確認へ進みます。

## 7. 変更後のメッセージを確認する

1. ブラウザで`http://localhost:8081/login`を開く
2. `user1`と`.env`の`APP_SEED_USER_PASSWORD`でログインする
3. 今日の状態が「未出勤」であることを確認する
4. 「出勤」ボタンを押す
5. 画面上部に「本日の出勤を受け付けました」と表示されることを確認する

古い「出勤しました」が表示される場合は、[第8章の「Javaの変更が反映されない」](./08-troubleshooting.md#9-javaの変更が反映されない)を確認してください。

## 8. 停止と後片付け

データを残して停止する通常の方法は、次のとおりです。

```bash
docker compose down
```

次回は、次のコマンドで再開できます。

```bash
docker compose up -d
```

Javaコードをさらに変更した後は、`--build`を付けます。

```bash
docker compose up -d --build
```

研修データを含めて完全に初期化したい場合だけ、削除対象を確認してから次を使用します。

```bash
docker compose down -v
```

`down -v`はDBデータを削除します。通常の停止では使用しません。

## 9. この章の完了チェック

- [ ] 一般ユーザーの出勤・退勤・勤怠一覧を確認した
- [ ] 管理者のアカウント管理・勤怠管理を確認した
- [ ] APIからユーザー一覧を取得した
- [ ] `docker compose down`後もデータが残ることを確認した
- [ ] `docker compose down -v`がDBデータを削除することを理解した
- [ ] Java側のメッセージを1行変更した
- [ ] `docker compose up -d --build`で変更を反映した
- [ ] 「本日の出勤を受け付けました」と表示された
- [ ] 通常の停止とvolumeを削除する停止を区別できる

[前へ：コンテナ化ハンズオン](./03-container-handson.md) ｜ [次へ：Docker Hubへのpush](./05-dockerhub-push.md)
