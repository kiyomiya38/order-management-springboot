# Docker Hub Push ハンズオン（Compose実施済み前提）

## 目的
- すでにローカルで動作確認済みの `attendance-management` アプリを、Docker Hub に push できるようになる
- 受講生自身でタグ運用（`latest` と `v1.0.0`）を実施できるようになる
- 認証（`docker login`）と push 失敗時の基本トラブルシュートを理解する

前提:
- `docker compose up -d --build` まで完了していること
- Docker Hub アカウントを作成済みであること

---

## 1. 構成

### 1-1. 今回扱うイメージ
- ローカルイメージ: `order-management-springboot-app:latest`
- Push先: `<あなたのDockerHubID>/attendance-management`
- 公開設定: `public`

### 1-2. 今回のゴール
1. `latest` を push する
2. `v1.0.0` を push する
3. Docker Hub 上で2つのタグが見えることを確認する

---

## 2. 事前準備（ローカルPC側）

### 2-1. コマンド確認
```bash
docker -v
docker compose version
```

### 2-2. 作業フォルダへ移動
```bash
cd ~/order-management-springboot
pwd
```

### 2-3. ローカルイメージ確認
```bash
docker images | grep -E "order-management-springboot-app|attendance-management"
```

`order-management-springboot-app` が見つからない場合:
```bash
docker compose build app
```

---

## 3. Docker Hub 側の準備

### 3-1. リポジトリ作成
Docker Hub にログインし、次を作成:
- Repository name: `attendance-management`
- Visibility: `Public`

補足:
- push時に自動作成させる運用もありますが、初学者は先にUIで作成した方が確認しやすいです。

### 3-2. 変数設定（Git Bash）
```bash
DOCKERHUB_ID=<あなたのDockerHubID>
APP_IMAGE_LOCAL=order-management-springboot-app
APP_IMAGE_REMOTE=${DOCKERHUB_ID}/attendance-management
```

確認:
```bash
echo "$APP_IMAGE_REMOTE"
```

---

## 4. タグ付け

### 4-1. `latest` タグ
```bash
docker tag ${APP_IMAGE_LOCAL}:latest ${APP_IMAGE_REMOTE}:latest
```

### 4-2. `v1.0.0` タグ
```bash
docker tag ${APP_IMAGE_LOCAL}:latest ${APP_IMAGE_REMOTE}:v1.0.0
```

### 4-3. タグ確認
```bash
docker images | grep "${DOCKERHUB_ID}/attendance-management"
```

期待:
- `latest`
- `v1.0.0`

---

## 5. Docker Hub 認証

### 5-1. ログイン
```bash
docker login
```

入力:
- Username: Docker Hub のユーザー名
- Password: パスワード または Access Token

推奨:
- 実運用では Password ではなく Access Token を使う

### 5-2. ログイン確認（任意）
```bash
docker info | grep Username
```

---

## 6. Push 実行

### 6-1. `latest` を push
```bash
docker push ${APP_IMAGE_REMOTE}:latest
```

### 6-2. `v1.0.0` を push
```bash
docker push ${APP_IMAGE_REMOTE}:v1.0.0
```

### 6-3. Docker Hub 画面確認
ブラウザで下記を開く:
- `https://hub.docker.com/r/<あなたのDockerHubID>/attendance-management/tags`

確認ポイント:
- `latest` がある
- `v1.0.0` がある

---

## 7. トラブルシュート

### 症状: `denied: requested access to the resource is denied`
原因:
- ログインユーザーと push先ユーザーが不一致
- リポジトリ名のスペル誤り

対処:
```bash
docker logout
docker login
echo ${APP_IMAGE_REMOTE}
```

### 症状: `no such image`
原因:
- `order-management-springboot-app:latest` が存在しない
- `docker tag` 前に build していない

対処:
```bash
docker compose build app
docker images | grep order-management-springboot-app
```

### 症状: `unauthorized: incorrect username or password`
原因:
- 認証情報ミス
- Token権限不足

対処:
- Docker Hub の Access Token を再発行して再ログイン

### 症状: Push が途中で失敗する
原因:
- ネットワーク不安定
- Docker Hub 側の一時障害

対処:
```bash
docker push ${APP_IMAGE_REMOTE}:latest
```
（再実行で回復することがある）

---

## 8. この演習と実運用の差分

実運用では次を追加検討します。

1. タグ戦略の厳密化（`v1.0.0`, `v1.0.1`, Git SHA など）
2. CI/CD で自動 build & push（GitHub Actions など）
3. Multi-arch イメージ（`linux/amd64`, `linux/arm64`）配布
4. 署名・SBOM・脆弱性スキャン
5. private リポジトリ運用時の pull 認証設計

---

## 9. 完了条件
- `docker tag` で `latest` と `v1.0.0` を作成できた
- `docker login` で認証できた
- Docker Hub に `latest` と `v1.0.0` を push できた
- Docker Hub の Tags 画面で2タグが確認できた
