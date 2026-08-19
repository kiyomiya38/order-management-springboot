# 06 Kubernetes CI/CDデプロイ課題

[前へ：Docker Hubへのpush](./05-dockerhub-push.md) ｜ [次へ：Kubernetesマルチテナント演習](./07-kubernetes-multitenant-handson.md)

## この課題について

この章は、手順どおりに操作するハンズオンではなく、これまでの学習内容を組み合わせる総合課題です。

完成済みの勤怠管理アプリケーションと、別の演習で構築したGitHub Actions、GitOps、Argo CDの環境を利用し、GitOps経由でKubernetesへデプロイできる構成を完成させてください。

このファイルには、課題の条件と完了基準だけを掲載します。コマンド、YAMLの記述例、具体的な作成手順、解答例は掲載しません。解答は、後日別ファイルで共有します。

## 課題のゴール

- アプリケーションリポジトリの`main`ブランチへのpushを起点にCIを実行する
- Spring Bootアプリの新しいimageをDocker Hubへpushする
- CIから別のGitOpsリポジトリにあるimage参照を更新する
- GitOpsリポジトリの状態をArgo CDからKubernetesへ反映する
- Ingressを経由して勤怠管理アプリケーションを利用できるようにする
- MariaDBのデータを永続化する

## 想定する全体構成

次の流れが完成後の構成です。

```text
アプリケーションリポジトリ
mainブランチへpush
        |
        v
既存のGitHub Actions
  |                         |
  | imageをbuildしてpush    | image参照を更新してpush
  v                         v
Docker Hub              別のGitOpsリポジトリ
Publicリポジトリ                 |
                                 v
                         既存のArgo CD
                                 |
                                 v
                         Kubernetesクラスタ
                         ├─ ingress-nginx
                         ├─ Spring Bootアプリ
                         ├─ MariaDB
                         └─ local-pathの永続ストレージ
```

この図は各仕組みの役割と処理の流れを示したものです。リポジトリ名、ファイル名、Kubernetesリソースの構成方法は指定しません。

## 使用する既存環境

この課題では、次の環境が準備済みであることを前提とします。

- AWS EC2上へkubeadmで構築したKubernetesクラスタ
- 導入済みのingress-nginx
- 導入済みのlocal-path-provisioner
- 第5章までに使用した完成版Spring BootアプリとDockerfile
- Spring Bootアプリ用のDocker Hub Publicリポジトリ
- `main`ブランチへのpushで動作する既存のGitHub Actionsワークフロー
- Kubernetes用YAMLを管理する既存のGitOpsリポジトリ
- GitOpsリポジトリを参照する既存のArgo CD環境

受講者ごとに独立した環境を使用します。ほかの受講者とnamespaceやホスト名を合わせる必要はありません。

## 課題

上記の既存環境を利用し、次の要件をすべて満たすKubernetes CI/CD構成を完成させてください。

### 1. CIの流れ

- アプリケーションリポジトリの`main`ブランチへのpushを起点にする
- リポジトリ内のDockerfileを使用してSpring Bootアプリのimageを作成する
- 作成したimageを、自分のDocker Hub Publicリポジトリへpushする
- 今回作成したimageを以前のimageと区別できるtagを付ける
- Docker Hubの認証情報は、既存の方法で安全に扱う

使用するワークフロー名、Secrets名、imageのtagの付け方は指定しません。これまでの演習で作成したCI構成を利用してください。

### 2. GitOpsリポジトリの更新

- Kubernetesへ反映するYAMLを、既存のGitOpsリポジトリで管理する
- CIから、Spring Bootアプリのimage参照を新しく作成したimageへ更新する
- 更新結果をGitOpsリポジトリへpushする
- GitOpsリポジトリのimage参照を前回の値から更新し、Docker Hubへpushされた今回のimageと一致させる

完成状態を作るために、Kubernetes上のimage参照だけを手作業で書き換えてはいけません。GitOpsリポジトリを正しい状態へ更新し、その内容をデプロイへつなげてください。

### 3. Kubernetes上のアプリケーション

次の条件を満たすように、Spring BootアプリとMariaDBをデプロイしてください。

| 項目 | 条件 |
| --- | --- |
| Spring Bootアプリのimage | CIがDocker Hubへpushしたimage |
| Spring Bootアプリのレプリカ数 | 1 |
| MariaDBのimage | `mariadb:11.4` |
| MariaDBのレプリカ数 | 1 |
| MariaDBのデータ保存 | `local-path`を使用し、Podを作り直してもデータが残ること |
| 外部からのアクセス | 既存のingress-nginxを使用すること |
| Spring BootとMariaDBの接続 | Kubernetes上で読み書きできること |

使用するKubernetesリソースの種類、リソース名、YAMLファイルの分割方法は指定しません。要件を満たす構成を自分で判断してください。

### 4. Argo CDによるデプロイ

- 既存のArgo CD環境を使用する
- Argo CDが既存のGitOpsリポジトリにある対象YAMLを参照できるようにする
- GitOpsリポジトリへ反映された状態をKubernetesへデプロイする
- 最終的にArgo CD上で対象アプリケーションが`Synced`かつ`Healthy`になるようにする

Argo CDのApplication名、監視するパス、同期方法、同期オプションは指定しません。既存環境の構成に合わせてください。

### 5. アプリケーションの動作

IngressのURLからアプリケーションへアクセスし、少なくとも次の動作を確認してください。

- ログイン画面が表示される
- 一般ユーザーまたは管理者でログインできる
- 画面から登録したデータをMariaDBへ保存できる
- 保存したデータを画面から再度確認できる
- MariaDBのPodを作り直した後も、保存済みのデータを確認できる

## 秘密情報の扱い

この課題ではDocker HubのPublicリポジトリを使用します。次の情報をPublicリポジトリ、Docker image、チャット、画面キャプチャ、CIの公開ログへ掲載してはいけません。

- Docker HubのPersonal Access Token
- GitHub Secretsに登録した値
- `kubeconfig`の内容
- MariaDBのパスワード
- そのほかの認証情報

GitHubとKubernetesで認証情報を管理する方法は、この課題では指定しません。これまでの演習で使用した方法に従ってください。誤って公開した場合は、値を削除するだけでなく、その認証情報を直ちに無効化して新しいものへ交換します。

## 指定しない項目

次の項目には、この課題共通の値を指定しません。受講者ごとの既存環境や、これまでの演習で採用した構成に合わせてください。

- アプリケーションリポジトリとGitOpsリポジトリの名前
- GitHub Actionsのワークフロー名と処理の分割方法
- `main`ブランチへ反映する変更内容
- Docker Hubのnamespace、リポジトリ名、tagの付け方
- Kubernetesのnamespace、リソース名、YAMLファイル名
- Ingressのホスト名
- Argo CDのApplication名、監視パス、同期設定
- 認証情報の登録名と参照方法

## この課題に含めないこと

- AWS EC2やKubernetesクラスタの新規構築
- ingress-nginxやlocal-path-provisionerのインストール
- GitHub Actions、GitOps、Argo CDの基礎説明
- Java側の表示メッセージの変更
- 自動テストの追加
- ロールバック演習
- Worker Nodeの障害に備えたMariaDBの高可用性
- 作業手順、コマンド、YAMLの解答例

## 完了確認

次をすべて確認できたら課題完了です。

- [ ] アプリケーションリポジトリの`main`ブランチへのpushでCIが動作した
- [ ] 新しいSpring BootアプリのimageがDocker Hubへpushされた
- [ ] GitOpsリポジトリのYAMLにあるimage参照が前回の値から新しいimageへ更新された
- [ ] Argo CDで対象アプリケーションが`Synced`と表示された
- [ ] Argo CDで対象アプリケーションが`Healthy`と表示された
- [ ] Spring BootアプリとMariaDBのPodが正常に稼働している
- [ ] IngressのURLからログイン画面を表示できた
- [ ] アプリケーションからMariaDBへデータを保存し、再度表示できた
- [ ] MariaDBのPodを作り直した後も保存済みデータが残っていた
- [ ] 手作業によるKubernetes上だけの変更ではなく、GitOpsリポジトリが正しい完成状態になっている

## 講師による確認

提出用ファイル、画面キャプチャ、作業報告書は不要です。講師が受講者ごとの環境を確認します。

確認時に、次の状態をその場で示せるようにしてください。

- Docker Hubに対象のimageが存在する
- GitOpsリポジトリのimage参照が更新されている
- Argo CDが`Synced`かつ`Healthy`である
- Kubernetes上でSpring BootアプリとMariaDBが稼働している
- Ingressからアプリケーションへログインできる
- MariaDBのPodを作り直してもデータが保持される

[前へ：Docker Hubへのpush](./05-dockerhub-push.md) ｜ [次へ：Kubernetesマルチテナント演習](./07-kubernetes-multitenant-handson.md)
