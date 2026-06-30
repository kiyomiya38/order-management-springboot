# Java研修 講師事前チェック

研修開始前に、講師または教材管理者が実施します。

## 自動確認

PowerShellでリポジトリルートから実行します。

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify-java-handson.ps1
```

確認対象:

- Java / `javac` / Mavenコマンド
- Java-20Aの`record` / `enum`完成コードのコンパイル
- Java-20Bの`HttpServer`完成コードのコンパイル
- Java-21のMaven/JUnitテスト
- Java教材内のローカルリンクとコードフェンス
- 本編・補講とミニ演習解答の対応
- Java-20A/20Bに旧`Lesson0`表記が残っていないこと

## 実施順の確認

Spring Bootまで進む場合は、次の順序で実施します。

```text
Java-01〜04 -> 04A -> Java-05〜17 -> 17A
-> Java-18〜20 -> 20A -> 20B -> Java-21
```

Java-09AはJava-09直後の実施を強く推奨します。その他の補講は受講者の不足項目に応じて選択します。

## 講師リハーサル

| 範囲 | 確認内容 |
| --- | --- |
| Java-01〜08 | 作成、コンパイル、実行、条件分岐、繰り返し、配列、メソッドを説明できる |
| Java-09〜17A | クラス分割、コンストラクタ、カプセル化、継承、例外を説明できる |
| Java-18〜20 | List / Set / Map、Stream、Javadoc検索を説明できる |
| Java-20A | `record`と`enum`の完成コードを実行できる |
| Java-20B | `curl`でGET、POST、400、404、405を確認できる |
| Java-21 | 正常2件・異常2件の4テストを成功させられる |

## 参考資料と解答の扱い

- 教材冒頭のPDF/PPTXリンクを講師PCで開けることを確認する
- Java-20のOracle Javadocへ接続できることを確認する
- ミニ演習解答は、受講者が実行結果と考え方を説明した後に参照させる
- Mermaid対応のMarkdown表示環境を使用する

## 合格判定

- 自動確認がすべて成功する
- 必修教材のミニ演習を講師が一度実行している
- [カリキュラム評価ガイド](../../curriculum-assessment-guide.md)のJava質問へ回答できる進行になっている
- 実施日・実施者・結果を研修運用記録へ残す
