# 00 Docker Desktopをインストールする

[教材の入口へ戻る](./README.md) ｜ [次へ：Spring Bootの概要](./01-spring-boot-overview.md)

## この章のゴール

Windows 11へWSL 2とDocker Desktopを導入し、Git BashからDockerコンテナを起動できることを確認します。

この章が終わった時点で、次のコマンドが正常に実行できれば完了です。

```bash
docker run --rm hello-world
```

## Docker DesktopとWSL 2の関係

この教材では、Linuxコンテナを使用します。LinuxコンテナをWindows 11上で動かすために、Docker DesktopはWSL 2というWindowsの機能を利用します。

- **Docker Desktop**：Windows上でDockerを使いやすくするアプリケーション
- **WSL 2**：Windows上でLinuxの仕組みを動かすための機能
- **コンテナ**：アプリケーションと、その実行に必要なものをまとめて動かす単位

この段階では、詳しい仕組みを暗記する必要はありません。「WindowsとLinuxコンテナの間をWSL 2がつないでいる」と考えてください。

詳しい仕組みは、Docker公式の[Docker Desktop WSL 2 backend on Windows](https://docs.docker.com/desktop/features/wsl/)で確認できます。

## 作業を始める前の確認

### 1. 必要な条件

この教材は、次の条件を満たすPCを対象にしています。

- Docker Desktopが現在サポートしている64ビット版Windows 11
- Windows Updateが適用されている
- メモリが8GB以上
- WSLがバージョン2.1.5以上
- BIOSまたはUEFIでハードウェア仮想化が有効
- インターネットへ接続できる

Windows 11の細かな対応バージョンは今後変更される可能性があります。インストール前に、Docker公式の[Windows向けインストール手順とシステム要件](https://docs.docker.com/desktop/setup/install/windows-install/)を確認してください。

Windowsのバージョンは、`Windows`キーと`R`キーを同時に押し、`winver`と入力して確認できます。

メモリ容量と仮想化の状態が分からない場合は、研修担当者または社内IT担当者へ確認してください。仮想化が無効な場合、BIOSまたはUEFIの設定変更が必要です。PCによって操作方法が異なるため、自己判断で変更しないでください。

### 2. インターネット接続

初回は、次のデータをインターネットからダウンロードします。

- WSL本体とLinux関連データ
- Docker Desktop
- 動作確認用のDockerイメージ
- 後の章で使用するJava、Maven、MariaDBのDockerイメージ
- Mavenが使用するライブラリ

初回のビルドや起動には時間がかかる場合があります。ダウンロード中はDocker Desktopやターミナルを閉じないでください。

### 3. 会社で利用する場合のライセンス確認

Docker Desktopは、利用者や組織の条件によって有料サブスクリプションが必要です。会社の規模、利用目的、契約状況を受講者自身で判断せず、研修担当者または社内IT担当者へ確認してください。

最新の条件は、Docker公式の[Docker Desktopの利用条件](https://docs.docker.com/desktop/setup/install/windows-install/#docker-desktop-terms)で確認できます。

## 管理者権限が分からない場合

Docker Desktopの推奨される**ユーザー単位インストール**は、インストールと更新自体には管理者権限を必要としません。

ただし、そのPCで初めてWSL 2を有効にするときは、1回だけ管理者権限が必要です。

この後の手順で、次のいずれかが発生した場合は作業を止めてください。

- 管理者のユーザー名やパスワードを求められた
- ユーザーアカウント制御の画面で許可できない
- 組織のポリシーによりインストールが拒否された
- BIOSまたはUEFIで仮想化を有効にする必要がある

管理者権限を回避しようとせず、研修担当者または社内IT担当者へ次のように依頼します。

> Docker Desktopの講義でWSL 2の有効化が必要です。Windows 11で`wsl --install`または`wsl --update`を管理者として実行できるようにしてください。

WSL 2がすでに正しく導入されていれば、受講者の権限だけでユーザー単位のDocker Desktopをインストールできる場合があります。

## Step 1：WSLの状態を確認する

最初は、通常のPowerShellを開きます。まだ管理者として起動する必要はありません。

次のコマンドを実行してください。

```powershell
wsl --version
```

### バージョン情報が表示された場合

`WSL version`などのバージョン情報が表示されることを確認します。WSLのバージョンは**2.1.5以上**が必要です。

続けて、次のコマンドを実行します。

```powershell
wsl -l -v
```

Linuxディストリビューションが表示された場合は、`VERSION`列が`2`であることを確認してください。

一覧が空でも、この教材では問題ありません。Docker Desktopは、特定のLinuxディストリビューションを受講者が用意していなくても利用できます。

WSLが2.1.5以上であれば、[Step 3：Docker Desktopをダウンロードする](#step-3docker-desktopをダウンロードする)へ進んでください。

### バージョン情報が表示されない場合

次のような場合は、WSLのインストールまたは更新が必要です。

- `wsl`が見つからないというエラーが表示される
- バージョン情報ではなくヘルプだけが表示される
- WSLのバージョンが2.1.5より古い

[Step 2：WSLをインストールまたは更新する](#step-2wslをインストールまたは更新する)へ進んでください。

## Step 2：WSLをインストールまたは更新する

このStepでは、PowerShellを**管理者として**起動します。管理者として実行できない場合は、前述の[管理者権限が分からない場合](#管理者権限が分からない場合)に従って作業を止め、担当者へ依頼してください。

Microsoftの公式手順は、[WSLのインストール](https://learn.microsoft.com/windows/wsl/install)で確認できます。

### WSLがインストールされていない場合

管理者として起動したPowerShellで、次のコマンドを実行します。

```powershell
wsl --install
```

必要なWindows機能とWSLがインストールされます。既定ではUbuntuもインストールされます。

コマンドから再起動を求められたら、開いているファイルを保存してWindowsを再起動してください。再起動を後回しにすると、Docker Desktopが正しく動かない場合があります。

Ubuntuの初回起動時にLinux用のユーザー名とパスワードを求められた場合は、画面の案内に従って設定します。このパスワードは入力中に文字が表示されませんが、入力は行われています。

### WSLが古い場合

管理者として起動したPowerShellで、次のコマンドを実行します。

```powershell
wsl --update
```

更新後に再起動を求められた場合は、Windowsを再起動してください。

### WSLを再確認する

再起動後は通常のPowerShellを開き、次の2つを実行します。

```powershell
wsl --version
wsl -l -v
```

次の状態になっていれば、WSLの準備は完了です。

- WSLのバージョン情報が表示される
- WSLのバージョンが2.1.5以上である
- Linuxディストリビューションが表示される場合、`VERSION`列が`2`である

期待した結果にならない場合は、もう一度`wsl --update`を実行します。それでも解決しない場合は、[トラブルシューティング](08-troubleshooting.md)を参照してください。

## Step 3：Docker Desktopをダウンロードする

1. Webブラウザで、Docker公式の[Docker Desktop for Windowsのインストールページ](https://docs.docker.com/desktop/setup/install/windows-install/)を開きます。
2. Windows向けのインストーラーをダウンロードします。
3. ダウンロードしたファイルが`Docker Desktop Installer.exe`であることを確認します。

この教材は一般的なx86_64またはAMD64のWindows 11 PCを想定しています。Arm版Windowsを使用している場合は、自己判断で異なるインストーラーを選ばず、研修担当者へ確認してください。

## Step 4：Docker Desktopをインストールする

1. `Docker Desktop Installer.exe`を起動します。
2. インストール方式を尋ねられた場合は、推奨される**ユーザー単位（Per-user）**を選びます。
3. バックエンドを尋ねられた場合は、**WSL 2**を使用する設定を選びます。
4. 表示される案内に従ってインストールを完了します。

ユーザー単位インストールは、通常`%LOCALAPPDATA%\Programs\DockerDesktop`へインストールされます。講義ではWindowsコンテナやHyper-Vバックエンドを使用しません。

Docker Desktopの画面や項目名は、バージョンによって変わる場合があります。教材と画面の表現が少し異なる場合は、「ユーザー単位」と「WSL 2」を選ぶことを基準にしてください。

管理者の資格情報を求められて先へ進めない場合はキャンセルし、社内IT担当者へ相談してください。

## Step 5：Docker Desktopを起動する

1. WindowsのスタートメニューからDocker Desktopを起動します。
2. 初回起動時に利用条件が表示されたら、組織の確認済み方針に従って内容を確認します。
3. Docker Desktopの準備が完了するまで待ちます。

Docker Desktopは、インストール直後に自動起動しない場合があります。以降の演習を始める前にも、Docker Desktopが起動済みであることを確認してください。

WSL 2対応PCでは、WSL 2ベースのエンジンが通常は既定で使用されます。設定項目が表示されない場合もあります。画面上の項目名を探し続けるのではなく、次のコマンドで実際の動作を確認します。

## Step 6：Git Bashからバージョンを確認する

ここからはGit Bashを使用します。

Git Bashを開き、次のコマンドを実行してください。

```bash
docker --version
```

期待する結果は、`Docker version`から始まるバージョン情報が表示されることです。

続けて、Docker Composeを確認します。

```bash
docker compose version
```

期待する結果は、`Docker Compose version`から始まるバージョン情報が表示されることです。

最後に、DockerがLinuxコンテナを実行する状態になっているか確認します。

```bash
docker info --format '{{.OSType}}'
```

期待する結果は次のとおりです。

```text
linux
```

`docker`コマンドが見つからない場合は、Git Bashをすべて閉じてから開き直してください。

Docker Desktopへ接続できないというエラーが表示された場合は、Docker Desktopの起動が完了しているか確認してください。

## Step 7：最初のコンテナを実行する

Git Bashで次のコマンドを実行します。

```bash
docker run --rm hello-world
```

初回は`hello-world`イメージをインターネットからダウンロードするため、少し時間がかかります。

実行結果に、Dockerが正しく動作したことを知らせるメッセージが表示されれば成功です。

このコマンドでは、次の処理が行われています。

1. `hello-world`という小さなDockerイメージを探す
2. PCにまだなければインターネットからダウンロードする
3. イメージからコンテナを起動する
4. コンテナ内のメッセージを表示する
5. `--rm`により、終了したコンテナを自動削除する

コンテナは自動削除されますが、ダウンロードしたイメージはPCに残ります。次回は同じイメージを再ダウンロードせずに実行できます。

## 完了チェック

次のすべてを確認してください。

- [ ] Windows 11がDocker Desktopのシステム要件を満たしている
- [ ] メモリが8GB以上ある
- [ ] ハードウェア仮想化が有効である
- [ ] `wsl --version`でバージョン情報が表示される
- [ ] WSLのバージョンが2.1.5以上である
- [ ] Docker Desktopをユーザー単位でインストールした
- [ ] Docker Desktopを起動した
- [ ] `docker --version`が成功した
- [ ] `docker compose version`が成功した
- [ ] `docker info --format '{{.OSType}}'`で`linux`と表示された
- [ ] `docker run --rm hello-world`が成功した

すべて確認できたら、[Spring Bootとコンテナの概要](01-spring-boot-overview.md)へ進んでください。

## 参考資料

- [Install Docker Desktop on Windows（Docker公式）](https://docs.docker.com/desktop/setup/install/windows-install/)
- [Docker Desktop WSL 2 backend on Windows（Docker公式）](https://docs.docker.com/desktop/features/wsl/)
- [WSLのインストール（Microsoft公式）](https://learn.microsoft.com/windows/wsl/install)

[教材の入口へ戻る](./README.md) ｜ [次へ：Spring Bootの概要](./01-spring-boot-overview.md)
