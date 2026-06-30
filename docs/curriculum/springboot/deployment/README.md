# Spring Boot 環境演習

Lesson7まで完了したアプリを、実行環境へ配置する選択式の演習です。
研修環境と所要時間に合わせて、AまたはBの一方を選択します。両方の実施も可能です。

| 演習 | 教材 | 目的 |
| --- | --- | --- |
| A | [VirtualBox 2VM](./virtualbox/README.md) | Nginx、systemd、MariaDBを使う実サーバー風構成を体験する |
| B | [Docker Compose](./docker-compose/README.md) | App + MariaDBのコンテナ構成とVolume永続化を体験する |

## 作業領域

Lesson7の完成物を直接変更せず、環境演習ごとの作業領域へ複製します。

```text
stages/lesson07
├── 複製先: stages/deployment-vm
└── 複製先: stages/deployment-docker
```

## 共通の完了条件

- Lesson7のFlywayマイグレーションを引き継いで起動できる
- Spring BootからMariaDBへ接続できる
- 画面へアクセスし、ログイン後の主要機能を操作できる
- アプリ、ネットワーク、DBのどこで失敗したか切り分けられる
