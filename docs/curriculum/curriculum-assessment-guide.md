# カリキュラム評価ガイド

この資料は、Java初学者が Spring Boot まで進む過程で、講師と受講者が同じ基準で進捗を判断するためのガイドです。
「動いた」だけでは合格にせず、説明できるか、原因を追えるか、次の教材に進んでよいかを確認します。

## 評価レベル
各チェックポイントは、次の4段階で評価します。

| レベル | 判定 | 状態 |
| --- | --- | --- |
| 4 | 発展可 | 自力で実装でき、なぜその設計か説明でき、エラー時に原因を切り分けられる |
| 3 | 合格 | 教材を見ながら実装でき、主要な処理の流れを口頭で説明できる |
| 2 | 要復習 | 写経で動かせるが、処理の流れやエラー原因の説明が弱い |
| 1 | 停止 | コンパイル、起動、画面確認のいずれかで自力復旧できない |

次の週へ進む目安は、必修項目がすべてレベル3以上です。
レベル2が残る場合は、次へ進む前に復習対象を1つに絞って補強します。

## 週別チェックポイント

| 週 | 範囲 | 必須確認 |
| --- | --- | --- |
| Week 1 | Java基礎 | `main`、変数、条件分岐、繰り返し、配列、メソッドを使って小さな処理を作れる |
| Week 2 | OOP | クラス分割、インスタンス、カプセル化、継承、ポリモーフィズム、例外の使いどころを説明できる |
| Week 3 | Java API読解 | Javadocを根拠に実装し、`record` / `enum` / `HttpServer` / HTTPステータスを説明できる |
| Week 4 | フロントエンド | HTMLフォーム、CSS、DOM操作、イベント処理、`fetch` / JSON通信の最小形を説明できる |
| Week 5 | web-app前半 | `fetch` からJava APIへ届き、JSONで返り、DOMが更新される流れを説明できる |
| Week 6 | web-app後半 | CRUD、バリデーション、状態遷移、メモリ保存、Spring Bootへの置き換えを説明できる |
| Week 7 | Spring Boot導入 | Maven、`@Controller`、Thymeleaf、Entity、Repository、Service、DB保存の流れを説明できる |
| Week 8 | Spring Boot強化 | 業務ルール、認証、認可、Serviceテストの目的を説明できる |
| Week 9 | REST/API/運用 | `@RestController`、DTO、JSONエラー応答、Flyway、デプロイ/コンテナ化の位置づけを説明できる |

## レビューで聞く質問

### Java
1. `static` メソッドとインスタンスメソッドの違いは何か
2. フィールドを `private` にする理由は何か
3. 例外を握りつぶすと何が困るか
4. `List` と配列をどう使い分けるか

### JavaScript / Web
1. `fetch` は何をしているか
2. `await response.json()` の前に `response.ok` を見る理由は何か
3. API更新とDOM再描画を分ける理由は何か
4. クライアント側チェックだけでは不十分な理由は何か

### Spring Boot
1. `@Controller` と `@RestController` の違いは何か
2. Controllerに業務ルールを詰め込みすぎると何が困るか
3. Repositoryは何を担当し、Serviceは何を担当するか
4. `@Valid` とDTOを使う理由は何か
5. FlywayでSQL履歴を残す理由は何か

## 受講者の提出物
講師レビューでは、次の3点だけを確認します。

1. 動作確認結果
   - 実行コマンド、ブラウザ表示、Networkタブ、curl結果など
2. 説明メモ
   - 「画面入力 -> API -> 保存 -> 表示」の流れを短く書く
3. つまずきメモ
   - 発生したエラー、原因、直した内容を書く

コード量の多さでは評価しません。
教材の目的に関係ない装飾や追加機能も、必修確認が終わるまでは評価対象にしません。

## 戻る判断
次の状態なら、先へ進まず復習に戻します。

| 状態 | 戻る場所 |
| --- | --- |
| Javaのクラス分割が説明できない | `docs/curriculum/java/java-handson/java-09-multi-class-development.md` 以降 |
| `fetch` とJSONの流れが説明できない | `docs/curriculum/javascript/javascript-fetch-json.md` |
| HTTPメソッドの使い分けが曖昧 | `docs/curriculum/web-app(簡易版)/lesson2.md` と `docs/curriculum/web-app(簡易版)/lesson3.md` |
| サーバー側バリデーションの理由が説明できない | `docs/curriculum/web-app(簡易版)/lesson4.md` |
| Spring Bootの層構造が説明できない | `docs/curriculum/springboot/lesson02/lesson2.md` |
| 認証と認可の違いが曖昧 | `docs/curriculum/springboot/lesson05/lesson5.md` |
| JSON APIとThymeleafの違いが曖昧 | `docs/curriculum/web-app(簡易版)/bridge-to-springboot.md` と `docs/curriculum/springboot/lesson08/lesson8.md` |

## 講師運用メモ
- 1回のレビューで直す弱点は最大2つに絞る
- 「どのファイルのどの処理で起きたか」を受講者に説明させる
- コード修正前に、ブラウザConsole、Network、サーバーログ、DB確認のどこを見るかを聞く
- 合格後の発展課題は任意にし、必修導線を遅らせない

