# 完成版Spring Bootアプリをコンテナ化し、Kubernetes CI/CDと環境分離へつなぐ

## この教材について

この教材では、完成済みの勤怠管理アプリケーションを使い、Spring BootアプリケーションとMariaDBをDockerコンテナで動かします。動作確認後は、Spring BootアプリのimageをDocker Hubへpushします。続いて、学習済みのGitHub Actions、GitOps、Argo CDを利用して、GitOps経由でKubernetesへデプロイする課題に取り組みます。最後に、開発環境と運用環境を別のNamespaceへ分離するハンズオンを行います。

Javaのプログラムを一から作る講義ではありません。最初から用意されているアプリケーションを確認した後、受講者自身が次のファイルを作成します。

- `Dockerfile`
- `.dockerignore`
- `docker-compose.yml`
- `.env`

コンテナ化の演習では、次の1コマンドでアプリケーションとデータベースをビルドして起動します。

```bash
docker compose up -d --build
```

アプリの動作確認後は、Spring Bootアプリのimageへバージョンを付け、Docker HubのPublicリポジトリへpushします。

第6章のKubernetes CI/CD課題は、手順や解答例を掲載しない総合課題です。既存のCI/CD環境へこのアプリケーションを組み込み、提示された完了条件を満たしてください。

第7章は、第6章で受講者自身が作成したKubernetes用YAMLを利用する手順型ハンズオンです。`attendance-dev`と`attendance-prod`へ環境を分け、同じクラスタ内で設定、データ、使用できるリソース量を分離します。

## 学習目標

この教材を終えると、次のことができるようになります。

- Spring Bootが何をする仕組みなのかを、簡単な言葉で説明できる
- 完成版アプリケーションの主な機能とフォルダ構成を確認できる
- Dockerfileを使ってSpring Bootアプリケーションのイメージを作成できる
- Docker Composeを使ってSpring BootとMariaDBをまとめて起動できる
- パスワードなどの設定値を環境変数で渡せる
- コンテナの起動状態とログを確認できる
- Volumeを使ってデータを保持できる
- Javaの表示メッセージを変更し、再ビルドして反映を確認できる
- コンテナの停止、再起動、データの初期化ができる
- Spring Bootアプリのimageへtagを付け、Docker Hubへpushできる
- 学習済みのGitHub Actions、GitOps、Argo CDを利用し、Spring BootアプリをKubernetesへデプロイできる
- Namespaceを使い、開発環境と運用環境のKubernetesリソースを分けられる
- ResourceQuotaとLimitRangeの役割を説明し、環境ごとの上限を設定できる
- 開発環境で確認した同じimageを運用環境へ反映できる

## 対象者と前提環境

第0章から第5章までの対象者は、Javaの基本文法を学習済みで、Dockerを初めて使用する方です。

第6章と第7章は発展内容です。別の演習でKubernetes、GitHub Actions、GitOps、Argo CDを学習し、既存環境を操作できる方を対象とします。

使用する環境は次のとおりです。

- Windows 11
- Docker Desktop
- WSL 2
- Git Bash
- Visual Studio Code
- インターネットへ接続できる環境
- Docker Hubアカウントと、書き込み権限を持つPersonal Access Token

Docker DesktopのインストールとWSL 2の初回有効化では、管理者権限が必要になる場合があります。管理者権限を使用できるか分からない場合の対応は、最初の章で説明します。

第6章と第7章では、別の演習で構築・学習済みの次の環境も使用します。

- AWS EC2上へkubeadmで構築したKubernetesクラスタ
- `kubectl`から対象のKubernetesクラスタを操作できる環境
- 導入済みのingress-nginx
- 導入済みのlocal-path-provisioner
- GitHub Actionsを使用する既存のCIワークフロー
- Kubernetes用YAMLを管理する既存のGitOpsリポジトリ
- GitOpsリポジトリからデプロイする既存のArgo CD環境

## 使用するフォルダ

教材を読むフォルダと、実際に操作するフォルダは異なります。

| 用途 | フォルダ |
| --- | --- |
| この教材 | `docs/curriculum/springboot-container` |
| 受講者が操作する完成版Javaアプリ | `stages/springboot-container` |
| 以前のSpring Boot教材 | `docs/curriculum/springboot` |

`stages/springboot-container`には、勤怠管理アプリケーションのJavaコードが完成した状態で用意されています。コンテナ関連のファイルは受講者が演習で作成します。

以前のSpring Boot教材は削除や変更をせず、そのまま残します。

第6章と第7章では既存のアプリケーションリポジトリとGitOpsリポジトリも使用します。リポジトリ名や作業フォルダは受講者ごとに異なるため、この教材では固定しません。

## 学習の順番

上から順番に進めてください。

1. [Docker Desktopをインストールする](00-docker-desktop-install.md)
2. [Spring Bootとコンテナの概要](01-spring-boot-overview.md)
3. [完成版アプリケーションを確認する](02-completed-app-overview.md)
4. [Spring BootとMariaDBをコンテナ化する](03-container-handson.md)
5. [操作、コード変更、再ビルドを行う](04-operation-and-rebuild.md)
6. [Spring BootアプリのimageをDocker Hubへpushする](05-dockerhub-push.md)
7. [Kubernetes CI/CDデプロイ課題に取り組む](06-kubernetes-cicd-assignment.md)
8. [Namespaceで開発環境と運用環境を分ける](07-kubernetes-multitenant-handson.md)
9. [トラブルシューティング](08-troubleshooting.md)

第5章まででエラーが発生した場合は、作業中の章にある確認事項を見た後、[トラブルシューティング](08-troubleshooting.md)を参照してください。

## この教材で扱うこと

- Spring Bootの初心者向け概要
- 完成済み勤怠管理アプリケーションの確認
- Docker DesktopとWSL 2の導入
- Dockerfileの作成
- Docker ComposeによるSpring BootとMariaDBの起動
- マルチステージビルド
- `.env`と環境変数
- Docker Volumeによるデータ保持
- コンテナのログ確認、停止、再起動、初期化
- Javaの表示メッセージ変更と再ビルド
- Docker HubのPublicリポジトリ作成
- Personal Access Tokenを使ったDocker CLIへのログイン
- Spring Bootアプリimageへのtag付けとDocker Hubへのpush
- Docker Hub画面での`1.0.0`タグ確認
- 既存のGitHub Actions、GitOps、Argo CDを利用するKubernetes CI/CD総合課題
- Ingressを経由したSpring Bootアプリの公開
- local-path-provisionerを利用したMariaDBデータの永続化
- `attendance-dev`と`attendance-prod`による環境分離
- NamespaceごとのResourceQuotaとLimitRange
- 開発環境から運用環境への同一imageの反映

## この教材で扱わないこと

- Spring Bootアプリケーションを一から実装すること
- Controller、Service、Repositoryなどの詳しい実装方法
- AWS EC2やKubernetesクラスタそのものの構築手順
- ingress-nginxやlocal-path-provisionerの導入手順
- GitHub ActionsやArgo CDを一から構築する手順
- Kubernetes CI/CD課題の解答例
- NetworkPolicyによるNamespace間通信の遮断
- RBACやArgo CD AppProjectによる厳密な権限分離
- MariaDBの高可用性、バックアップ、災害復旧
- 本番環境向けのセキュリティ設計や運用設計
- PDF教材の作成や更新

Spring Bootの説明は、初学者が全体像をつかむための概要に絞ります。実際のコードを細かく追いかけることよりも、「どの仕組みが何を担当しているのか」を理解することを目指します。

## コマンドを入力する場所

WSL 2の導入で管理者権限が必要な操作だけは、管理者として起動したPowerShellを使用します。

第0章から第5章までの教材中のコマンドは、特に指定がない限りGit Bashで実行します。Git Bashを開いたら、最初に作業フォルダへ移動してください。

```bash
cd ~/order-management-springboot/stages/springboot-container
```

現在位置は次のコマンドで確認できます。

```bash
pwd
```

`/c/Users/ユーザー名/order-management-springboot/stages/springboot-container`のようなパスが表示されれば、正しい場所です。

第6章には実行コマンドを掲載しません。KubernetesやGitOps環境の操作場所は、これまでに使用した演習環境に従ってください。

第7章のコマンドは、`kubectl`からクラスタを操作でき、GitOpsリポジトリを編集できるBash環境で実行します。
