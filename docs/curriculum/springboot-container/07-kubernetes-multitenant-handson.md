# 07 Namespaceで開発環境と運用環境を分ける

[前へ：Kubernetes CI/CDデプロイ課題](./06-kubernetes-cicd-assignment.md) ｜ [次へ：トラブルシューティング](./08-troubleshooting.md)

## この章について

この章では、第6章で受講者自身が完成させたKubernetes用YAMLを利用し、開発環境と運用環境を別のNamespaceへ分けます。

第6章の解答となるSpring Boot、MariaDB、Ingress、CIワークフローの完成版は掲載しません。自分が作成した構成を残したまま、環境分離に必要な変更を加えてください。

この章は、上から順番に進める手順型ハンズオンです。

> この章の「運用環境」は研修用の環境です。`prod`という名前を付けても、高可用性、バックアップ、TLS、監視などが自動的に本番品質になるわけではありません。

> この演習ではNamespaceやPVCを削除しません。Namespaceを削除すると、その中のリソースがまとめて削除され、StorageClassの設定によってはデータも失われます。

## この章のゴール

- `attendance-dev`と`attendance-prod`の2つのNamespaceを作成する
- 同じ名前のKubernetesリソースを、Namespaceごとに分けて配置する
- 開発環境と運用環境でConfigMap、Secret、MariaDB、PVC、Ingressを分ける
- ResourceQuotaでNamespace全体の使用量に上限を設定する
- LimitRangeでコンテナとPVCの標準値・上限を設定する
- Argo CDから2つの環境を別々に管理する
- 開発環境で確認した同じimageを運用環境へ反映する
- 一方の環境で登録したデータが、もう一方へ混ざらないことを確認する

## 1. Namespaceによるマルチテナントを理解する

この演習では、1つのKubernetesクラスタを共有しながら、開発環境と運用環境を論理的に分けます。

```text
1つのKubernetesクラスタ
|
+-- クラスタ全体で共有
|   +-- Worker Node
|   +-- ingress-nginx
|   +-- StorageClass local-path
|
+-- Namespace: attendance-dev
|   +-- Spring Bootアプリ
|   +-- MariaDB
|   +-- Service / Ingress
|   +-- ConfigMap / Secret
|   +-- PVC
|   +-- ResourceQuota / LimitRange
|
+-- Namespace: attendance-prod
    +-- Spring Bootアプリ
    +-- MariaDB
    +-- Service / Ingress
    +-- ConfigMap / Secret
    +-- PVC
    +-- ResourceQuota / LimitRange
```

Namespaceの中に作られるリソースは、`リソースの種類 + 名前 + Namespace`で区別されます。そのため、開発環境と運用環境で同じService名や同じPVC名を使用できます。

| Namespaceの中にあるリソース | クラスタ全体のリソース |
| --- | --- |
| Deployment | Node |
| StatefulSet | PersistentVolume |
| Service | StorageClass |
| ConfigMap | IngressClass |
| Secret | CustomResourceDefinition |
| PersistentVolumeClaim | Namespace自身 |
| Ingress |  |
| ResourceQuota |  |
| LimitRange |  |

次のコマンドでも、Namespaceの対象になるリソースを確認できます。

```bash
kubectl api-resources --namespaced=true
kubectl api-resources --namespaced=false
```

### Namespaceだけでは分離されないもの

Namespaceは、リソースの名前と管理範囲を分ける仕組みです。次のものを自動的には分離しません。

- Namespace間のネットワーク通信
- Kubernetesユーザーの操作権限
- Worker Node
- ingress-nginx
- StorageClassとPersistentVolume

NetworkPolicyによる通信遮断やRBACによる権限分離は、この演習では行いません。この章で作るのは、Namespaceを使った論理的なマルチテナント構成です。

## 2. 完成後のGitOps構成を確認する

GitOpsリポジトリ内に、開発環境用と運用環境用のディレクトリを作ります。

```text
GitOpsリポジトリ
└── attendance-management
    ├── namespaces
    │   └── namespaces.yaml
    ├── dev
    │   ├── 第6章で作成したYAML一式
    │   ├── namespace-policy.yaml
    │   └── 必要に応じてkustomization.yaml
    └── prod
        ├── 第6章で作成したYAML一式
        ├── namespace-policy.yaml
        └── 必要に応じてkustomization.yaml
```

既存のGitOpsリポジトリに別の命名規則がある場合は、その規則に合わせて構いません。ただし、開発環境と運用環境が別のパスになるようにしてください。

Argo CDでは、2つの環境を別のApplicationとして管理します。

| 項目 | 開発環境 | 運用環境 |
| --- | --- | --- |
| Application名 | `attendance-dev` | `attendance-prod` |
| GitOpsの参照先 | `attendance-management/dev` | `attendance-management/prod` |
| デプロイ先 | `attendance-dev` | `attendance-prod` |
| Ingressのホスト名 | 開発環境専用 | 運用環境専用 |
| MariaDBのデータ | 開発環境専用 | 運用環境専用 |

imageは次の流れで反映します。

```text
アプリケーションリポジトリのmainへpush
                |
                v
既存CIがimageをDocker Hubへpush
                |
                v
開発環境のimage参照を更新
                |
                v
attendance-devで動作確認
                |
                v
同じimage参照を運用環境へ反映
                |
                v
attendance-prodへデプロイ
```

開発環境と運用環境の間でimageを作り直しません。運用環境へは、開発環境で確認したimageと同じdigestを反映する方法を推奨します。

既存CIがtagで管理している場合は、commit SHAなどから作る一意なtagを使用し、そのtagへ別のimageを上書きしないでください。最後にPodの`imageID`を比較し、実際に取得されたimageが同一であることを確認します。

## 3. 作業前の状態を確認する

`kubectl`を使用でき、GitOpsリポジトリへpushできるBash環境で操作します。

最初にKubernetesクラスタを確認します。

```bash
kubectl cluster-info
kubectl get nodes
kubectl get storageclass
kubectl get ingressclass
```

次を確認してください。

- Worker Nodeが`Ready`である
- local-path-provisionerが使用するStorageClassを確認できる
- ingress-nginxが使用するIngressClassを確認できる
- 第6章のアプリケーションがArgo CDで`Synced`かつ`Healthy`である

次にGitOpsリポジトリへ移動し、作業中の変更がないことを確認します。

```bash
cd YOUR_GITOPS_REPOSITORY
git status --short
```

`YOUR_GITOPS_REPOSITORY`は、受講者が使用しているGitOpsリポジトリのパスへ置き換えます。

変更が表示された場合は、自分の作業内容か確認してください。内容が分からない変更を削除したり、上書きしたりしてはいけません。

## 4. 環境別ディレクトリを作成する

GitOpsリポジトリのルートで次を実行します。

```bash
mkdir -p attendance-management/namespaces
mkdir -p attendance-management/dev
mkdir -p attendance-management/prod
```

第6章で自分が作成したYAMLのうち、次のようなアプリケーション用リソースを`dev`と`prod`へそれぞれコピーします。

- Spring Bootアプリ
- Spring Bootアプリ用Service
- MariaDB
- MariaDB用Service
- ConfigMap
- PVCまたはStatefulSetの`volumeClaimTemplates`
- Ingress

次のものはコピーしません。

- Argo CDのApplication
- 既存環境のNamespace
- 認証情報の値が書かれたファイル

Kustomizeを使用している場合は、既存の`kustomization.yaml`もコピーし、後で追加する`namespace-policy.yaml`を管理対象へ加えます。

### ここまでの確認

- [ ] `attendance-management/dev`を作成した
- [ ] `attendance-management/prod`を作成した
- [ ] 両方へ第6章のYAMLをコピーした
- [ ] Secretの値をGitOpsリポジトリへコピーしていない

## 5. 2つのNamespaceを作成する

作成ファイル：

```text
attendance-management/namespaces/namespaces.yaml
```

ファイル全体を次の内容にします。

```yaml
# 開発環境のリソースをまとめるNamespace
apiVersion: v1
kind: Namespace
metadata:
  name: attendance-dev
  labels:
    app.kubernetes.io/part-of: attendance-management
    environment: development
---
# 運用環境のリソースをまとめるNamespace
apiVersion: v1
kind: Namespace
metadata:
  name: attendance-prod
  labels:
    app.kubernetes.io/part-of: attendance-management
    environment: production
```

Namespaceは、Argo CDがアプリケーションを配置する前に必要です。この演習では、Gitで管理しながら、最初の1回だけ`kubectl`で作成します。

最初に、同名のNamespaceがすでに存在しないか確認します。

```bash
kubectl get namespace attendance-dev attendance-prod \
  --ignore-not-found \
  --show-labels
```

どちらかが表示された場合は、既存リソースも確認します。

```bash
kubectl get deployment,statefulset,service,pvc,ingress -n attendance-dev
kubectl get deployment,statefulset,service,pvc,ingress -n attendance-prod
```

自分がこの演習で使用する環境ではない場合は、ここで作業を止めてください。既存のNamespaceへ別のアプリケーションを混在させてはいけません。

```bash
kubectl apply -f attendance-management/namespaces/namespaces.yaml
kubectl get namespace attendance-dev attendance-prod --show-labels
```

`attendance-dev`と`attendance-prod`が`Active`で表示されれば成功です。

> 既存のArgo CD構成で`CreateNamespace=true`やNamespaceのGitOps管理を使用している場合は、既存の方法を利用して構いません。同じNamespaceを別々のArgo CD Applicationから重複管理しないでください。

## 6. 開発環境へLimitRangeとResourceQuotaを追加する

作成ファイル：

```text
attendance-management/dev/namespace-policy.yaml
```

ファイル全体を次の内容にします。

```yaml
# LimitRangeは、コンテナ1つとPVC1つに対する標準値・範囲を設定する
apiVersion: v1
kind: LimitRange
metadata:
  name: attendance-limits
  namespace: attendance-dev
  annotations:
    # Argo CDでResourceQuotaやアプリより先に反映する
    argocd.argoproj.io/sync-wave: "-2"
spec:
  limits:
    # コンテナにrequests/limitsがない場合の標準値
    - type: Container
      defaultRequest:
        cpu: 100m
        memory: 256Mi
      default:
        cpu: 500m
        memory: 768Mi
      min:
        cpu: 50m
        memory: 128Mi
      max:
        cpu: "2"
        memory: 2Gi
    # PVCが要求できる容量の範囲
    - type: PersistentVolumeClaim
      min:
        storage: 1Gi
      max:
        storage: 10Gi
---
# ResourceQuotaは、attendance-dev全体で使用できる合計量を制限する
apiVersion: v1
kind: ResourceQuota
metadata:
  name: attendance-quota
  namespace: attendance-dev
  annotations:
    # LimitRangeの後、アプリより前に反映する
    argocd.argoproj.io/sync-wave: "-1"
spec:
  hard:
    requests.cpu: "1"
    requests.memory: 2Gi
    limits.cpu: "2"
    limits.memory: 4Gi
    pods: "8"
    services: "8"
    configmaps: "10"
    secrets: "10"
    persistentvolumeclaims: "2"
    requests.storage: 10Gi
```

### LimitRangeとResourceQuotaの違い

```text
LimitRange
└─ コンテナ1つ、PVC1つの標準値・最小値・最大値

ResourceQuota
└─ Namespace全体のCPU、メモリ、Pod数、PVC数などの合計上限
```

ResourceQuotaでCPUやメモリを制限すると、コンテナに`requests`や`limits`が必要になります。この演習では、値を書いていないコンテナにもLimitRangeが標準値を設定します。

既存YAMLに明示的な`requests`や`limits`がある場合は、その値がLimitRangeの範囲内か確認してください。

## 7. 運用環境へLimitRangeとResourceQuotaを追加する

作成ファイル：

```text
attendance-management/prod/namespace-policy.yaml
```

ファイル全体を次の内容にします。

```yaml
# 運用環境のコンテナとPVCに対する標準値・範囲
apiVersion: v1
kind: LimitRange
metadata:
  name: attendance-limits
  namespace: attendance-prod
  annotations:
    argocd.argoproj.io/sync-wave: "-2"
spec:
  limits:
    - type: Container
      defaultRequest:
        cpu: 200m
        memory: 512Mi
      default:
        cpu: "1"
        memory: 1Gi
      min:
        cpu: 50m
        memory: 128Mi
      max:
        cpu: "2"
        memory: 2Gi
    - type: PersistentVolumeClaim
      min:
        storage: 1Gi
      max:
        storage: 20Gi
---
# attendance-prod全体で使用できる合計量
apiVersion: v1
kind: ResourceQuota
metadata:
  name: attendance-quota
  namespace: attendance-prod
  annotations:
    argocd.argoproj.io/sync-wave: "-1"
spec:
  hard:
    requests.cpu: "2"
    requests.memory: 4Gi
    limits.cpu: "4"
    limits.memory: 8Gi
    pods: "12"
    services: "10"
    configmaps: "10"
    secrets: "10"
    persistentvolumeclaims: "3"
    requests.storage: 20Gi
```

この数値は研修用です。実際の運用では、アプリケーションの測定結果、同時利用者数、クラスタ容量などを基に決定します。

### ここまでの確認

- [ ] 開発環境の`namespace-policy.yaml`を作成した
- [ ] 運用環境の`namespace-policy.yaml`を作成した
- [ ] LimitRangeがResourceQuotaより先に反映される設定を確認した
- [ ] 既存YAMLのリソース指定がLimitRangeとResourceQuotaの範囲内である

## 8. 第6章のYAMLを環境別に変更する

`dev`と`prod`へコピーしたYAMLを、次の表に従って変更します。

| 確認項目 | 開発環境 | 運用環境 |
| --- | --- | --- |
| `metadata.namespace` | `attendance-dev` | `attendance-prod` |
| 環境を表すラベル | `environment: development` | `environment: production` |
| アプリ名 | 開発環境と分かる名前 | 運用環境と分かる名前 |
| セッションCookie名 | 開発環境専用 | 運用環境専用 |
| Ingressのホスト名 | 開発環境専用 | 運用環境専用 |
| Secret | `attendance-dev`内のSecret | `attendance-prod`内のSecret |
| MariaDBとPVC | `attendance-dev`内 | `attendance-prod`内 |
| Spring Bootのimage | CIが開発環境へ反映したimage | 開発確認後に同じdigest、または上書きしない一意tagを反映 |

### 8-1. Namespaceを設定する

Deployment、StatefulSet、Service、ConfigMap、PVC、Ingressなど、Namespaceの対象になるすべてのリソースを変更します。

- 開発環境の`metadata.namespace`は`attendance-dev`
- 運用環境の`metadata.namespace`は`attendance-prod`

Kustomizeを使用している場合は、各環境の`kustomization.yaml`に設定した`namespace`を使用して構いません。

Namespace、PersistentVolume、StorageClassなど、クラスタ全体のリソースへ`metadata.namespace`を追加してはいけません。

### 8-2. Service名とDB接続先を確認する

開発環境と運用環境では、同じService名を使用できます。

たとえばMariaDBのService名が`db`の場合、Spring Bootから`db:3306`へ接続すると、同じNamespaceにあるServiceが選ばれます。

次のように、別環境のNamespaceを含む接続先を指定してはいけません。

```text
開発環境から db.attendance-prod.svc.cluster.local へ接続する
運用環境から db.attendance-dev.svc.cluster.local へ接続する
```

DB接続先、DBユーザー、DBパスワードを参照するSecretが、同じNamespace内のMariaDBと一致していることを確認します。

### 8-3. Ingressのホスト名を分ける

開発環境と運用環境で異なるホスト名を使用します。

```text
開発環境：受講者の開発環境用ホスト名
運用環境：受講者の運用環境用ホスト名
```

実際のホスト名は、受講者ごとの既存環境に合わせます。同じホスト名とパスを2つのIngressへ設定すると、ingress-nginx上で競合する可能性があります。

### 8-4. ConfigMapとSecretの参照を分ける

ConfigMapとSecretはNamespaceごとのリソースです。同じ名前を使用しても、値は開発環境と運用環境で別々に管理されます。

次の値は環境ごとに分けます。

- `APP_NAME`
- `SESSION_COOKIE_NAME`
- MariaDBのパスワード
- 初期管理者のパスワード
- 初期一般ユーザーのパスワード

### 8-5. PVCを分ける

PVCもNamespaceごとのリソースです。同じPVC名を使用しても、`attendance-dev`と`attendance-prod`では別のPVCとPersistentVolumeが作成されます。

両環境で、導入済みのlocal-path-provisionerが使用するStorageClassを指定してください。

> local-pathのデータはWorker Node上へ保存されます。Namespaceを分けても、Node障害への高可用性やバックアップは提供されません。

第6章の既存MariaDBデータは、新しく作る開発環境と運用環境へ自動移行されません。この演習では新しいデータベースとして開始し、データ移行は行いません。

## 9. 環境ごとのSecretを作成する

認証情報をPublicリポジトリへ保存しないため、この演習ではSecretの値をGitOpsリポジトリへ書きません。

既存YAMLが別のSecret名やキー名を使用している場合は、次の`attendance-secrets`とキー名を既存YAMLに合わせて変更してください。

この手順は、新しいNamespaceと新しいMariaDBを初期化するときに1回だけ行います。既存PVCを使用中にSecretのMariaDBパスワードだけを変更しても、MariaDB内部のユーザーパスワードは自動変更されません。既存Secretがある場合は、同じ値を用意できない限り、このコマンドを再実行しないでください。

```bash
kubectl get secret attendance-secrets -n attendance-dev --ignore-not-found
kubectl get secret attendance-secrets -n attendance-prod --ignore-not-found
```

Secretが表示された場合のパスワード変更は、MariaDB側のパスワード更新と合わせて行う必要があります。パスワードローテーションはこの演習の対象外です。

### 9-1. 開発環境のSecret

次をBashで実行します。入力した値は画面に表示されません。

```bash
read -s -p "dev MARIADB_PASSWORD: " DEV_DB_PASSWORD
echo
read -s -p "dev MARIADB_ROOT_PASSWORD: " DEV_ROOT_PASSWORD
echo
read -s -p "dev APP_SEED_ADMIN_PASSWORD: " DEV_ADMIN_PASSWORD
echo
read -s -p "dev APP_SEED_USER_PASSWORD: " DEV_USER_PASSWORD
echo

if [[ -z "$DEV_DB_PASSWORD" || -z "$DEV_ROOT_PASSWORD" \
      || -z "$DEV_ADMIN_PASSWORD" || -z "$DEV_USER_PASSWORD" ]]; then
  echo "Secretの値は空欄にできません。最初から入力し直してください。" >&2
else
  kubectl create secret generic attendance-secrets \
    --namespace attendance-dev \
    --from-literal=MARIADB_PASSWORD="$DEV_DB_PASSWORD" \
    --from-literal=DB_PASSWORD="$DEV_DB_PASSWORD" \
    --from-literal=MARIADB_ROOT_PASSWORD="$DEV_ROOT_PASSWORD" \
    --from-literal=APP_SEED_ADMIN_PASSWORD="$DEV_ADMIN_PASSWORD" \
    --from-literal=APP_SEED_USER_PASSWORD="$DEV_USER_PASSWORD" \
    --dry-run=client -o yaml \
    | kubectl apply -f -
fi

unset DEV_DB_PASSWORD DEV_ROOT_PASSWORD DEV_ADMIN_PASSWORD DEV_USER_PASSWORD
```

### 9-2. 運用環境のSecret

開発環境とは異なる値を入力します。

```bash
read -s -p "prod MARIADB_PASSWORD: " PROD_DB_PASSWORD
echo
read -s -p "prod MARIADB_ROOT_PASSWORD: " PROD_ROOT_PASSWORD
echo
read -s -p "prod APP_SEED_ADMIN_PASSWORD: " PROD_ADMIN_PASSWORD
echo
read -s -p "prod APP_SEED_USER_PASSWORD: " PROD_USER_PASSWORD
echo

if [[ -z "$PROD_DB_PASSWORD" || -z "$PROD_ROOT_PASSWORD" \
      || -z "$PROD_ADMIN_PASSWORD" || -z "$PROD_USER_PASSWORD" ]]; then
  echo "Secretの値は空欄にできません。最初から入力し直してください。" >&2
else
  kubectl create secret generic attendance-secrets \
    --namespace attendance-prod \
    --from-literal=MARIADB_PASSWORD="$PROD_DB_PASSWORD" \
    --from-literal=DB_PASSWORD="$PROD_DB_PASSWORD" \
    --from-literal=MARIADB_ROOT_PASSWORD="$PROD_ROOT_PASSWORD" \
    --from-literal=APP_SEED_ADMIN_PASSWORD="$PROD_ADMIN_PASSWORD" \
    --from-literal=APP_SEED_USER_PASSWORD="$PROD_USER_PASSWORD" \
    --dry-run=client -o yaml \
    | kubectl apply -f -
fi

unset PROD_DB_PASSWORD PROD_ROOT_PASSWORD PROD_ADMIN_PASSWORD PROD_USER_PASSWORD
```

`MARIADB_PASSWORD`はMariaDB用、`DB_PASSWORD`はSpring Boot用です。この演習では同じパスワードを2つのキー名で登録します。既存YAMLで`secretKeyRef`を使って別のキーを明示的に対応付けている場合は、その構成に合わせてください。

Secretが2つのNamespaceへ別々に作成されたことを確認します。

```bash
kubectl get secret attendance-secrets -n attendance-dev
kubectl get secret attendance-secrets -n attendance-prod
```

Secretの値を表示するコマンドは実行しません。ターミナル出力や画面キャプチャへ認証情報を出さないでください。

既存環境でSealed SecretsやExternal Secretsなどを使用している場合は、手動作成の代わりに既存方式を利用して構いません。

## 10. YAMLを検証してGitOpsリポジトリへpushする

### 10-1. YAMLを検証する

通常のYAMLファイルを使用している場合は、次を実行します。

```bash
kubectl apply --dry-run=server -f attendance-management/dev
kubectl apply --dry-run=server -f attendance-management/prod
```

Kustomizeを使用している場合は、代わりに次を実行します。

```bash
kubectl apply --dry-run=server -k attendance-management/dev
kubectl apply --dry-run=server -k attendance-management/prod
```

`--dry-run=server`は、YAMLの構造とKubernetes APIで受け付けられる内容かを確認しますが、リソースを作成・変更しません。

この確認では、同じディレクトリにあるLimitRangeやResourceQuotaも保存されません。そのため、実際のPod作成時に適用される標準値や合計使用量までは確認できません。QuotaとLimitRangeの実効結果は、Argo CDで反映した後に`describe`とEventで確認します。

エラーが出た場合は、次を確認します。

- `metadata.namespace`が正しいか
- Namespaceが作成済みか
- 同じファイル内でYAMLの字下げが崩れていないか
- LimitRangeの最大値を超える`requests`や`limits`がないか
- ResourceQuotaの合計上限を超えていないか
- PVCの容量がLimitRangeの範囲内か

### 10-2. Gitへ登録する

認証情報が含まれていないことを確認してから、Gitへ登録します。

```bash
git status --short
git diff --check
git diff
git add attendance-management
git diff --cached
git commit -m "Add dev and prod Kubernetes environments"
git push
```

`git diff --cached`で、パスワード、Personal Access Token、`kubeconfig`の内容が含まれていないことを確認してからcommitしてください。

## 11. Argo CDで2つのApplicationを設定する

既存のArgo CD画面で、第6章のApplicationを参考に2つのApplicationを設定します。

第6章の既存Applicationが新しい`dev`または`prod`と同じpathやNamespaceを管理している場合は、リソースの管理が重複します。

- 既存Applicationがすでに`attendance-management/dev`と`attendance-dev`を管理している場合だけ、そのまま開発環境として利用する
- それ以外の場合は既存Applicationを変更せず、`attendance-dev`と`attendance-prod`を新しく作る

同じpathとNamespaceを複数のApplicationから管理してはいけません。

### 11-1. 開発環境

次の項目を設定します。

| 項目 | 設定 |
| --- | --- |
| Application名 | `attendance-dev` |
| Gitリポジトリ | 既存のGitOpsリポジトリ |
| branch | 既存のGitOps用branch |
| path | `attendance-management/dev` |
| デプロイ先クラスタ | 既存のKubernetesクラスタ |
| Namespace | `attendance-dev` |
| 同期設定 | 第6章で使用した既存設定 |

Argo CD画面で`NEW APP`または`CREATE APPLICATION`を選び、表の内容を入力してApplicationを作成します。既存Applicationがすでに同じpathとNamespaceを管理している場合だけ、新規作成せずにそのまま利用します。

### 11-2. 運用環境

次の項目を設定します。

| 項目 | 設定 |
| --- | --- |
| Application名 | `attendance-prod` |
| Gitリポジトリ | 既存のGitOpsリポジトリ |
| branch | 既存のGitOps用branch |
| path | `attendance-management/prod` |
| デプロイ先クラスタ | 既存のKubernetesクラスタ |
| Namespace | `attendance-prod` |
| 同期設定 | 第6章で使用した既存設定 |

開発環境と同様に、Argo CD画面から運用環境のApplicationを作成します。

自動同期を使用していない場合は、それぞれのApplicationを開いて`SYNC`を実行します。自動同期を使用している場合は、GitOpsリポジトリの変更が検出されるまで待ちます。

次を確認します。

- 2つのApplicationが同じpathを参照していない
- デプロイ先Namespaceが入れ替わっていない
- 使用するAppProjectが2つのNamespaceへのデプロイを許可している
- 両方のApplicationが`Synced`かつ`Healthy`である

AppProjectの権限エラーが出る場合は、これまでのArgo CD演習で使用した設定を確認してください。この章ではAppProjectやRBACの再構築は行いません。

既存Applicationの削除は、この演習では行いません。Argo CDの削除方法やfinalizerの設定によっては、管理対象のアプリやPVCまで削除されるためです。

## 12. CIの反映先を開発環境へ変更する

第6章で使用した既存CIが、GitOpsリポジトリ内のimage参照を更新する場所を確認します。

この章では、`main`ブランチへのpushで最初に更新する対象を次の場所にします。

```text
attendance-management/dev
```

CIワークフローの書き方、tagの付け方、GitOpsリポジトリへの認証方法は、これまでの演習で使用した方法をそのまま利用します。

運用環境の`attendance-management/prod`を、開発環境の確認前に自動更新しないでください。

### 開発環境へ反映する

1. アプリケーションリポジトリの`main`ブランチへ変更をpushする
2. CIが新しいimageをDocker Hubへpushしたことを確認する
3. GitOpsリポジトリの`dev`にあるimage参照が更新されたことを確認する
4. Argo CDの`attendance-dev`が`Synced`かつ`Healthy`になることを確認する
5. 開発環境のIngressからアプリケーションを操作する

## 13. 確認済みimageを運用環境へ反映する

CIがGitOpsリポジトリの開発環境を更新しているため、最初にローカルのGitOpsリポジトリを最新状態へ合わせます。

```bash
git status --short
```

変更が表示された場合は、commitまたは退避を行ってください。内容が分からない変更を上書きしてはいけません。

何も表示されないことを確認してから、最新状態を取得します。

```bash
git pull --ff-only
grep -Rni "image:" attendance-management/dev
grep -Rni "image:" attendance-management/prod
```

開発環境で正常に動作したSpring Boot imageの参照を確認します。digest形式を推奨します。

```text
Docker Hubユーザー名/リポジトリ名@sha256:...
```

既存CIがtag形式を使用している場合は、commit SHAなどから作った一意で上書きしないtagであることを確認します。

運用環境のYAMLにあるSpring Boot image参照を、開発環境で確認したものと同じ値へ変更します。MariaDBの`mariadb:11.4`を変更しないでください。

```text
開発環境で確認したimage
        =
運用環境へ反映するimage
```

GitOpsリポジトリで変更内容を確認してpushします。

```bash
git status --short
git diff -- attendance-management/prod
git add attendance-management/prod
git diff --cached
git commit -m "Promote attendance image to prod"
git push
```

Argo CDで`attendance-prod`への反映を確認します。

- `Synced`である
- `Healthy`である
- 運用環境のPodが、開発環境で確認したものと同じdigestのimageを使用している

## 14. Namespaceごとのリソースを確認する

両方のNamespaceを並べて確認します。

```bash
kubectl get deployment,statefulset,service,pvc,ingress -n attendance-dev
kubectl get deployment,statefulset,service,pvc,ingress -n attendance-prod
```

同じ名前のリソースが、2つのNamespaceへ別々に存在していれば正常です。

Podと使用しているimageも確認します。

```bash
kubectl get pods -n attendance-dev -o wide
kubectl get pods -n attendance-prod -o wide
kubectl get pods -n attendance-dev -o jsonpath='{range .items[*]}{.metadata.name}{" => "}{range .spec.containers[*]}{.image}{" "}{end}{"\n"}{end}'
kubectl get pods -n attendance-prod -o jsonpath='{range .items[*]}{.metadata.name}{" => "}{range .spec.containers[*]}{.image}{" "}{end}{"\n"}{end}'
kubectl get pods -n attendance-dev -o jsonpath='{range .items[*]}{.metadata.name}{" => "}{range .status.containerStatuses[*]}{.name}{"="}{.imageID}{" "}{end}{"\n"}{end}'
kubectl get pods -n attendance-prod -o jsonpath='{range .items[*]}{.metadata.name}{" => "}{range .status.containerStatuses[*]}{.name}{"="}{.imageID}{" "}{end}{"\n"}{end}'
```

Spring Bootコンテナの`imageID`に含まれるdigestが、開発環境と運用環境で一致することを確認します。tag名が同じでも`imageID`が違う場合は、同一imageではありません。

ResourceQuotaとLimitRangeを確認します。

```bash
kubectl get resourcequota,limitrange -n attendance-dev
kubectl get resourcequota,limitrange -n attendance-prod
kubectl describe resourcequota attendance-quota -n attendance-dev
kubectl describe resourcequota attendance-quota -n attendance-prod
kubectl describe limitrange attendance-limits -n attendance-dev
kubectl describe limitrange attendance-limits -n attendance-prod
```

ResourceQuotaの`Used`は現在の使用量、`Hard`は上限です。LimitRangeでは、コンテナやPVCへ設定される範囲を確認できます。

## 15. Ingressとデータの分離を確認する

### 15-1. 2つのIngressを確認する

```bash
kubectl get ingress -n attendance-dev
kubectl get ingress -n attendance-prod
```

ブラウザで、それぞれのホスト名へアクセスします。

- 開発環境のホスト名で、開発環境の画面が開く
- 運用環境のホスト名で、運用環境の画面が開く
- 2つの環境で`APP_NAME`などの表示を分けた場合は、正しい環境名が表示される

### 15-2. MariaDBのデータが混ざらないことを確認する

1. 開発環境へ管理者でログインする
2. 開発環境だけに確認用ユーザーを1人作成する
3. 運用環境へ管理者でログインする
4. 開発環境で作成したユーザーが運用環境に存在しないことを確認する
5. 運用環境だけに、別の確認用ユーザーを1人作成する
6. そのユーザーが開発環境に存在しないことを確認する

両方から同じデータが見える場合は、2つのSpring Bootアプリが同じMariaDBへ接続しています。DB接続先、ServiceのNamespace、Secretの参照先を確認してください。

### 15-3. 開発環境のMariaDB Podを作り直す

最初にPod名を確認します。

```bash
kubectl get pods -n attendance-dev
kubectl get pods -n attendance-prod
```

`DEV_DB_POD_NAME`を、開発環境のMariaDB Pod名へ置き換えて実行します。

削除前に、MariaDB PodがStatefulSetの管理下にあることを確認します。

```bash
kubectl get statefulset -n attendance-dev
kubectl get pod DEV_DB_POD_NAME \
  -n attendance-dev \
  -o jsonpath='{.metadata.ownerReferences[0].kind}{"\n"}'
```

`StatefulSet`と表示されない場合はPodを削除せず、第6章で作成したMariaDBの管理方法を確認してください。単独で作成したPodは、削除後に自動再作成されません。

```bash
kubectl delete pod DEV_DB_POD_NAME -n attendance-dev
kubectl get pods -n attendance-dev --watch
```

MariaDB Podが再作成され、`READY`が`1/1`、`STATUS`が`Running`になったら、`Ctrl + C`で監視を終了します。

readinessProbeを設定していない場合、`Running`だけではMariaDBが接続を受け付けられる状態とは判断できません。開発環境の画面へログインし、登録済みデータを表示できるところまで確認します。

確認する内容は次のとおりです。

- 開発環境のMariaDB Podが再作成される
- 開発環境で登録したデータが残っている
- 運用環境のPodは削除されていない
- 運用環境で登録したデータも残っている

PVCとPersistentVolumeを確認します。

```bash
kubectl get pvc -n attendance-dev
kubectl get pvc -n attendance-prod
kubectl get persistentvolume
```

PVCはNamespaceごと、PersistentVolumeはクラスタ全体のリソースとして表示されます。

## 16. うまくいかないとき

### `secrets "..." not found`

SecretはNamespaceごとに必要です。

```bash
kubectl get secret -n attendance-dev
kubectl get secret -n attendance-prod
```

アプリやMariaDBが参照するSecret名と、同じNamespace内に作成したSecret名を比較してください。

### `exceeded quota`または`Forbidden`

ResourceQuotaの上限、LimitRangeの最大値、PodやPVCが要求している値を確認します。

```bash
kubectl describe resourcequota attendance-quota -n attendance-dev
kubectl describe limitrange attendance-limits -n attendance-dev
kubectl get events -n attendance-dev --sort-by=.metadata.creationTimestamp
```

運用環境で発生した場合は、Namespaceを`attendance-prod`へ変えて確認します。

### 開発環境と運用環境で同じ画面が開く

- Ingressのホスト名が重複していないか
- DNSまたはhostsファイルが正しいIngressのアドレスを参照しているか
- Ingressのbackend Serviceが同じNamespace内にあるか

を確認します。

### 開発環境と運用環境でデータが混ざる

- DB接続先に別環境の完全修飾Service名を指定していないか
- 2つの環境が外部の同じMariaDBを参照していないか
- ServiceとMariaDBのSelectorが同じNamespace内で対応しているか

を確認します。

### PVCが`Pending`になる

```bash
kubectl get storageclass
kubectl get pvc -n attendance-dev
kubectl describe pvc -n attendance-dev
kubectl get events -n attendance-dev --sort-by=.metadata.creationTimestamp
```

StorageClass名、要求容量、local-path-provisionerの状態を確認してください。

### Argo CDが`OutOfSync`または`Degraded`になる

- Applicationのpathとデプロイ先Namespace
- AppProjectが許可するNamespace
- Secretが作成済みか
- ResourceQuotaまたはLimitRangeによる拒否
- Ingressホスト名の競合

を順番に確認します。

## 17. この章の完了チェック

- [ ] `attendance-dev`と`attendance-prod`を作成した
- [ ] 開発環境と運用環境のYAMLを別のGitOps pathへ配置した
- [ ] 2つの環境で同じリソース名を使用できる理由を説明できる
- [ ] Namespaceの対象になるリソースと、クラスタ全体のリソースを区別できる
- [ ] 開発環境と運用環境へ別々のSecretを作成した
- [ ] Ingressのホスト名を環境ごとに分けた
- [ ] MariaDBとPVCが環境ごとに分かれている
- [ ] ResourceQuotaでNamespace全体の上限を設定した
- [ ] LimitRangeでコンテナとPVCの標準値・範囲を設定した
- [ ] `attendance-dev`と`attendance-prod`がArgo CDで`Synced`かつ`Healthy`になった
- [ ] CIが最初に開発環境のimage参照を更新する
- [ ] digest、または上書きしない一意tagを使って、開発環境で確認したimageを運用環境へ反映した
- [ ] Spring Bootコンテナの`imageID`が開発環境と運用環境で一致した
- [ ] 開発環境と運用環境でデータが混ざらないことを確認した
- [ ] 開発環境のMariaDB Podを作り直してもデータが残った
- [ ] Namespaceだけではネットワークや権限を完全分離できないと説明できる
- [ ] Secretの値をGitOpsリポジトリへcommitしていない

## 参考資料

- [Kubernetes公式：Namespaces](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/)
- [Kubernetes公式：Resource Quotas](https://kubernetes.io/docs/concepts/policy/resource-quotas/)
- [Kubernetes公式：Limit Ranges](https://kubernetes.io/docs/concepts/policy/limit-range/)
- [Argo CD公式：Application Specification](https://argo-cd.readthedocs.io/en/latest/user-guide/application-specification/)
- [Argo CD公式：Projects](https://argo-cd.readthedocs.io/en/stable/user-guide/projects/)

[前へ：Kubernetes CI/CDデプロイ課題](./06-kubernetes-cicd-assignment.md) ｜ [次へ：トラブルシューティング](./08-troubleshooting.md)
