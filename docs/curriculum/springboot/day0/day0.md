# Day0（事前学習）HTML / CSS / Java 基礎（1日）

## 目的（Day0でできるようになること）
- HTMLの基本構造を理解し、静的ページを自分で作れる
- CSSの基本ルールを理解し、見た目を整えられる
- Javaの基本構文（クラス/メソッド/変数/条件分岐/ループ/コレクション）を体験できる
- Day1のSpring Boot演習に進む前提知識を持てる

## 0. 環境セットアップ（Windows / Git Bash）
Day0の最初に環境を揃えます。すでに導入済みの場合は確認だけ実施してください。

### 0-1. 必要ツール一覧
- JDK 17（Java実行環境）
- Maven 3.9+（ビルド/起動）
- VS Code（編集）
- Git for Windows（Git Bash含む）
- ブラウザ（Edge/Chromeなど）

### 0-2. JDK 17 のインストール
ダウンロードURL: `https://adoptium.net/temurin/releases/`

1. 「Eclipse Adoptium (Temurin 17)」の Windows x64 インストーラを入手
2. インストール実行（デフォルトでOK）
3. 新しい Git Bash で確認:
   ```bash
   java -version
   ```
4. `17.x` が表示されればOK

期待出力例（抜粋）:
```text
openjdk version "17.0.x" ...
OpenJDK Runtime Environment ...
OpenJDK 64-Bit Server VM ...
```

`java` が見つからない場合:
- Windowsの「環境変数」で `JAVA_HOME` を設定
- `Path` に `%JAVA_HOME%\bin` を追加
- Git Bash 再起動

### 0-3. Maven 3.9+ のインストール
ダウンロードURL: `https://maven.apache.org/download.cgi`

1. Apache Maven の「Binary zip」を入手
2. 例: `%USERPROFILE%\Documents\apache-maven-3.9.x` に展開
3. Windowsの「環境変数」で `MAVEN_HOME` を設定
4. `Path` に `%MAVEN_HOME%\bin` を追加
5. 新しい Git Bash で確認:
   ```bash
   mvn -version
   ```

期待出力例（抜粋）:
```text
Apache Maven 3.9.x (...)
Java version: 17.x, vendor: ...
```

補足:
- `Path` 追加後は Git Bash を再起動
- 反映されない場合は PC 再起動

### 0-4. VS Code のインストール
ダウンロードURL: `https://code.visualstudio.com/Download`

1. 公式サイトからインストーラを入手
2. インストール実行（デフォルトでOK）
3. 任意確認:
   ```bash
   code -v
   ```

### 0-5. Git Bash（Git for Windows）
ダウンロードURL: `https://git-scm.com/download/win`

1. 「Git for Windows」を入手
2. インストール実行（デフォルトでOK）
3. スタートメニューから Git Bash が起動できればOK
4. 確認:
   ```bash
   git --version
   ```

### 0-6. 作業フォルダ
Day0は本体アプリ本体とは別に、練習用フォルダで進めます。

```bash
mkdir -p practice/day0/html
mkdir -p practice/day0/java
```

VS Codeで開く（GUI）:
1. VS Code を起動
2. `ファイル` → `フォルダーを開く`
3. `.../order-management-springboot/practice/day0` を選択

---

## 1. HTML / CSS 基礎（午前）

### 1-1. HTMLとは（初心者向けに具体的に）
HTML は **「ブラウザに表示する画面の骨組み（構造）」** を書くための言語です。  
文章の見出し、段落、表、入力欄などを **タグ** で表し、ブラウザがそれを画面に変換して表示します。

#### HTMLの基本ルール
- **タグで囲む**：`<h1>見出し</h1>` のように、開始タグと終了タグで内容を包む  
- **属性で情報を足す**：`<input type="text">` のように追加情報を書く  
- **入れ子構造**：タグの中にタグを入れる（箱の中に箱を入れるイメージ）

#### HTMLは「構造」を書く
HTMLだけでは「見た目」はほぼ変えられません。  
見た目は **CSS** で担当します。  
この研修ではまず **HTMLで構造を作る → CSSで見た目を整える** を体験します。

#### 実際の書き方（最小例）
```html
<!doctype html>
<html lang="ja">
  <head>
    <meta charset="utf-8" />
    <title>サンプルページ</title>
  </head>
  <body>
    <h1>見出し</h1>
    <p>ここに文章を書きます。</p>
  </body>
</html>
```

##### 各行の意味
- `<!doctype html>` : 「これはHTMLです」とブラウザに伝える宣言
- `<html>` : ページ全体の箱
- `<head>` : 画面に直接表示されない設定（タイトルや文字コード）
- `<body>` : 画面に表示される本文
- `<h1>` : 見出し
- `<p>` : 段落（文章）

#### 今日のゴール
「HTMLのタグを見れば、**だいたい何が表示されるか想像できる**」状態を目指します。

### 1-2. 最初のHTMLを作る
作成ファイル: `practice/day0/html/index.html`  
この演習は「タグを1つ追加するごとにブラウザ確認」を行います。

共通操作（毎ステップ共通）:
1. コードを追記して保存  
2. ブラウザで `index.html` を再読み込み  
3. 何が変わったかを1行メモ

#### Step 0: 空の骨組みを作る
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
</body>
</html>
```

タグ解説:
- `<!doctype html>`: この文書がHTML5であることをブラウザに伝える
- `<html lang="ja">`: ページ全体を包む最上位タグ。`lang="ja"` は日本語ページの宣言
- `<head>`: 画面には直接出ない設定を書く領域
- `<meta charset="utf-8" />`: 文字コード指定。日本語の文字化け防止
- `<title>`: ブラウザタブに表示されるページ名
- `<body>`: 画面に表示される本文領域

コードの意味:
- まず「表示の土台」だけを作る段階。まだ本文がないので画面は空で正常
- `<head>` と `<body>` を分けるのは、設定と表示を混ぜないため
- `<タグ名>` は開始タグ、`</タグ名>` は終了タグ。開始と終了のペアで1つの要素を作る
- `<meta ... />` のように `/>` で終わるものは自己終了タグ（閉じタグが不要）
- `lang="ja"` のような `名前="値"` は属性。タグに追加情報を与える
- インデント（先頭の空白）はブラウザ表示には影響しないが、構造を読みやすくするために必須

確認ポイント:
- Step 0 の時点では `<body></body>` が空なので、ブラウザ表示が真っ白で正しい

#### Step 1: `<h1>` と `<p>` を追加
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <h1>勤怠管理（HTML/CSS練習）</h1>
  <p>このページはタグを理解するための練習用です。</p>
</body>
</html>
```

確認:
- 見出しが大きく表示されるか
- 段落が表示されるか

タグ解説:
- `<h1>`: ページの主見出し。通常1ページに1つが基本
- `<p>`: 段落（文章ブロック）

コードの意味:
- Step 0の空の`<body>`に「最小の表示要素」を置いて、表示できる状態を作っている
- この段階では文書の意味（見出し/説明）だけを追加している
- `h1` と `p` はどちらもブロック要素なので、通常は縦に並んで表示される
- タグ名は小文字で統一するのが実務上の基本

#### Step 2: `<div class="container">` で囲む
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <div class="container">
    <h1>勤怠管理（HTML/CSS練習）</h1>
    <p>このページはタグを理解するための練習用です。</p>
  </div>
</body>
</html>
```

確認:
- 見た目変化はほぼない（CSS前）
- 構造として「外側の箱」ができた

タグ解説:
- `<div>`: 意味を持たない汎用の箱。レイアウトやグルーピングに使う
- `class="container"`: CSSでこの箱を指定するための名前

コードの意味:
- 後でCSSを当てる対象を用意するため、見出しと段落を1つの箱でまとめている
- インデントを1段下げるのは「`div`の内側」を示すため

#### Step 3: `<header>` を追加
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>
  </div>
</body>
</html>
```

確認:
- 見た目はほぼ同じ
- headerは「ページ先頭の意味」を持つタグ

タグ解説:
- `<header>`: セクションやページのヘッダー領域（タイトル・説明など）
- `class="subtitle"`: サブタイトル用のCSS適用ポイント

コードの意味:
- `header`で「ページの冒頭情報」を明示し、構造を読み取りやすくしている
- `subtitle`クラス名を先に置いておくと、CSS追加時にそのまま装飾できる

#### Step 4: `<section>` と `<h2>` を追加
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>

    <section class="panel">
      <h2>今日の勤怠</h2>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>
  </div>
</body>
</html>
```

確認:
- 小見出し（`h2`）が表示されるか

タグ解説:
- `<section>`: 1つの話題を持つまとまり（例: 今日の勤怠）
- `class="panel"`: カード風デザインを当てるためのクラス
- `<h2>`: セクション見出し（`h1` より小さい）

コードの意味:
- 画面を「ヘッダー」と「今日の勤怠」の2ブロックに分け始めた段階
- `h1`（ページタイトル）と`h2`（セクションタイトル）で見出しの階層を作っている

#### Step 5: `<span>` を追加
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge">未出勤</span>
      </div>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>
  </div>
</body>
</html>
```

確認:
- 「未出勤」の文字が表示されるか

タグ解説:
- `<span>`: 行内の一部を装飾するためのタグ
- `class="status-badge"`: 状態バッジの見た目を適用するクラス
- `<div class="panel-header">`: 見出しとバッジを1行で並べるためのグループ

コードの意味:
- `h2`と状態ラベルを同じ行に置くために、`panel-header`という内側の箱を追加している
- `span`は「短い文字列だけを装飾する」目的に向いている

#### Step 6: フォームタグを追加（`label`, `input`, `button`）
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge">未出勤</span>
      </div>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>

    <section class="panel">
      <h2>簡単なフォーム</h2>
      <label>
        名前:
        <input type="text" name="username" />
      </label>
      <button>送信</button>
    </section>
  </div>
</body>
</html>
```

確認:
- 入力欄とボタンが表示されるか
- 入力欄に文字を打てるか

タグ解説:
- `<label>`: 入力項目の説明ラベル。入力欄と意味的に関連づく
- `<input type="text">`: 1行テキスト入力欄
- `name="username"`: フォーム送信時の項目名
- `<button>`: クリック可能なボタン

コードの意味:
- 「表示だけの画面」から「入力できる画面」へ段階を進めている
- `section`を分けることで、勤怠表示エリアと入力エリアを分離している

#### 補足: `form` / `action` / `method`（Day1以降で使用）
Step 6 の入力欄とボタンは、次のように `form` で囲むと送信できる形になる。

```html
<form action="/clock-in" method="post">
  <label>
    名前:
    <input type="text" name="username" />
  </label>
  <button type="submit">送信</button>
</form>
```

意味:
- `<form>`: 入力値をまとめて送る単位
- `action`: 送信先URL
- `method`: 送信方法（`get` は参照、`post` は登録/更新で使うことが多い）
- `button type="submit"`: フォーム送信ボタン

#### Step 7: リストタグを追加（`ul`, `li`）
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge">未出勤</span>
      </div>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>

    <section class="panel">
      <h2>簡単なフォーム</h2>
      <label>
        名前:
        <input type="text" name="username" />
      </label>
      <button>送信</button>
    </section>

    <h2>やること</h2>
    <ul>
      <li>出勤する</li>
      <li>退勤する</li>
      <li>一覧を確認する</li>
    </ul>
  </div>
</body>
</html>
```

確認:
- 箇条書きが3行表示されるか

タグ解説:
- `<ul>`: 順序なしリスト
- `<li>`: リストの1項目

コードの意味:
- 単発テキストではなく、複数項目を同じ形式で並べる表現に切り替えている
- 同じ種類の情報は`li`でそろえると、後で項目を増減しやすい

#### Step 8: テーブルタグを追加（`table`, `tr`, `th`, `td`）
`index.html` を次の内容に更新:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge">未出勤</span>
      </div>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>

    <section class="panel">
      <h2>簡単なフォーム</h2>
      <label>
        名前:
        <input type="text" name="username" />
      </label>
      <button>送信</button>
    </section>

    <h2>やること</h2>
    <ul>
      <li>出勤する</li>
      <li>退勤する</li>
      <li>一覧を確認する</li>
    </ul>

    <h2>勤怠サンプル一覧</h2>
    <table>
      <tr>
        <th>日付</th>
        <th>出勤</th>
        <th>退勤</th>
      </tr>
      <tr>
        <td>2026-02-05</td>
        <td>09:00</td>
        <td>18:00</td>
      </tr>
    </table>
  </div>
</body>
</html>
```

確認:
- 表形式で1行表示されるか

タグ解説:
- `<table>`: 表全体
- `<tr>`: 1行
- `<th>`: 見出しセル
- `<td>`: データセル

コードの意味:
- 「行と列」を持つデータは、段落ではなく表で表現するのが適切
- 1行目を`th`にすることで、データの意味（列名）が明確になる

#### Step 9: `head` 内タグの影響を確認
1. `<title>` を「勤怠管理 - タグ演習」に変更  
2. `<head>` に `<link rel="stylesheet" href="styles.css" />` を追加  
3. `<meta charset="utf-8" />` を一時的に削除して文字化け確認 → 戻す  
4. `<link rel="stylesheet" href="styles.css" />` を一時的に外して見た目確認 → 戻す

Step 9開始時の `index.html`（全文）:
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>タグ演習</title>
  <link rel="stylesheet" href="styles.css" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge">未出勤</span>
      </div>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>

    <section class="panel">
      <h2>簡単なフォーム</h2>
      <label>
        名前:
        <input type="text" name="username" />
      </label>
      <button>送信</button>
    </section>

    <h2>やること</h2>
    <ul>
      <li>出勤する</li>
      <li>退勤する</li>
      <li>一覧を確認する</li>
    </ul>

    <h2>勤怠サンプル一覧</h2>
    <table>
      <tr>
        <th>日付</th>
        <th>出勤</th>
        <th>退勤</th>
      </tr>
      <tr>
        <td>2026-02-05</td>
        <td>09:00</td>
        <td>18:00</td>
      </tr>
    </table>
  </div>
</body>
</html>
```

タグ解説:
- `<title>`: ページ名。検索結果やブックマーク名にも使われる
- `<meta charset>`: 文字コード設定。削除すると日本語が崩れることがある
- `<link rel="stylesheet">`: 外部CSSファイルを読み込む設定

コードの意味:
- `head`の設定が表示品質に直結することを確認する検証ステップ
- CSSリンク有無の比較で「HTML構造」と「見た目装飾」が分離されていることを体感する

#### Step 10: 最終形（この時点の `index.html` 完成版）
```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8" />
  <title>勤怠管理 - HTML/CSS練習</title>
  <link rel="stylesheet" href="styles.css" />
</head>
<body>
  <div class="container">
    <header>
      <h1>勤怠管理（HTML/CSS練習）</h1>
      <p class="subtitle">このページはタグを理解するための練習用です。</p>
    </header>

    <section class="panel">
      <div class="panel-header">
        <h2>今日の勤怠</h2>
        <span class="status-badge">未出勤</span>
      </div>
      <p>日付: 2026-02-05</p>
      <p>出勤時刻: -</p>
      <p>退勤時刻: -</p>
    </section>

    <section class="panel">
      <h2>簡単なフォーム</h2>
      <label>
        名前:
        <input type="text" name="username" />
      </label>
      <button>送信</button>
    </section>

    <h2>やること</h2>
    <ul>
      <li>出勤する</li>
      <li>退勤する</li>
      <li>一覧を確認する</li>
    </ul>

    <h2>勤怠サンプル一覧</h2>
    <table>
      <tr>
        <th>日付</th>
        <th>出勤</th>
        <th>退勤</th>
      </tr>
      <tr>
        <td>2026-02-05</td>
        <td>09:00</td>
        <td>18:00</td>
      </tr>
    </table>
  </div>
</body>
</html>
```

---

### 1-3. CSSを作る
作成ファイル: `practice/day0/html/styles.css`  
HTMLと同じく、CSSも「1つずつ追加して毎回ブラウザ確認」で進めます。

#### CSSとは（HTMLとの違い）
- HTMLは「画面の構造（何を表示するか）」を書く
- CSSは「画面の見た目（どう表示するか）」を書く
- 同じHTMLでも、CSSを変えると色・余白・配置が変わる

#### CSSの基本構文
```css
セレクタ {
  プロパティ名: 値;
}
```

例:
```css
h1 {
  color: blue;
}
```

この例の意味:
- `h1` は「`<h1>`タグに適用する」という対象指定（セレクタ）
- `color` は文字色の設定項目（プロパティ名）
- `blue` は設定値
- `;` は1つの設定文の終わり

#### この演習で見るCSSの考え方
- 「共通で使う値」を CSS変数（`--bg` など）で定義する
- `class`（例: `.panel`）で同じ見た目を再利用する
- レイアウトは段階的に追加する（全体 → パネル → フォーム → 表）

#### CSSが効かないときの基本ルール（カスケード）
- 同じ要素に複数のCSSが当たる場合、後に書いた宣言が優先される
- より具体的なセレクタ（例: `.panel p`）は、ざっくりしたセレクタ（例: `p`）より優先される
- `class` 指定は要素名指定より優先される

例:
```css
p { color: blue; }
.panel p { color: red; }
```

この場合、`.panel` の中にある `p` は赤になる。

共通操作（毎ステップ共通）:
1. `styles.css` を更新して保存  
2. ブラウザを再読み込み  
3. どこが変わったかを1行メモ

#### Step 0: 空ファイルを作る
`styles.css` を空で作成（中身なし）

確認:
- 見た目が素のHTML表示のまま

コード解説:
- CSSファイルが空でもページは表示される。HTMLだけで最低限は成立する
- ここは比較用の基準（ビフォー）を作る目的

#### Step 1: 色変数とページ全体の設定
`styles.css` を次の内容に更新:

```css
:root {
  --bg: #dbeafe;
  --text: #202124;
}

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}
```

確認:
- 背景色が変わる
- 文字フォントが変わる

コード解説:
- `:root`は変数置き場。色名を変数化すると後で一括変更しやすい
- `body`に全体設定を置くと、ページ全体に同じ見た目を適用できる
- CSSの書式は `プロパティ名: 値;`。`:` は「設定項目と値の区切り」、`;` は「その行の終わり」
- `--bg: #dbeafe;` は「背景色の変数に薄い青を入れる」
- `--text: #202124;` は「文字色の変数に濃いグレーを入れる」
- `--bg` / `--text` の `--` は CSS変数名であることを示す記法
- `#f6f6f2` は16進カラーコード（`#RRGGBB`）。`RR`赤, `GG`緑, `BB`青
- `color: var(--text);` は「文字色に`--text`の値を使う」
- `background: var(--bg);` は「背景色に`--bg`の値を使う」
- `var(--text)` / `var(--bg)` は「変数の値を取り出して使う」という意味
- `font-family: "Segoe UI", Tahoma, sans-serif;` は「優先順でフォントを選ぶ」

#### Step 2: レイアウトの外枠を設定
`styles.css` を次の内容に更新:

```css
:root {
  --bg: #dbeafe;
  --text: #202124;
  --muted: #6b7280;
}

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

header {
  margin-bottom: 16px;
}

.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}
```

確認:
- 画面中央寄せになる
- 余白ができる
- サブタイトル色が薄くなる

コード解説:
- `.container`はレイアウトの外枠。`max-width`と`margin: 0 auto`で中央寄せ
- `.subtitle`を分離することで、本文と補足情報を見た目で区別できる
- `max-width: 900px;` は「最大幅を900pxまで」に制限する
- `margin: 0 auto;` は「左右中央寄せ」。固定幅や最大幅と組み合わせて使う
- `padding: 24px;` は「内側の余白」を追加する
- `px` はピクセル単位。`0` は単位なしで書ける

#### Step 3: パネル（カード）表示を追加
`styles.css` を次の内容に更新:

```css
:root {
  --bg: #dbeafe;
  --panel: #ffffff;
  --text: #202124;
  --muted: #6b7280;
  --border: #e5e7eb;
}

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

header {
  margin-bottom: 16px;
}

.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}
```

確認:
- sectionがカード風（白背景・枠線）になる

コード解説:
- `.panel`は再利用する部品。複数の`section`に同じ見た目を適用できる
- `border`/`border-radius`/`padding`の組み合わせでカードUIを作っている
- `--panel: #ffffff;` はカード背景色（白）の変数
- `--border: #e5e7eb;` は枠線色の変数
- `background: var(--panel);` はカードの内側色を指定
- `border: 1px solid var(--border);` は「太さ1px / 実線 / 色」の順で枠線指定
- `border-radius: 8px;` は角を丸める（数値が大きいほど丸い）
- `padding: 16px;` はカード内の余白
- `margin-bottom: 16px;` はカード同士の縦間隔

#### Step 4: ヘッダー行と状態バッジを追加
`styles.css` を次の内容に更新:

```css
:root {
  --bg: #dbeafe;
  --panel: #ffffff;
  --text: #202124;
  --muted: #6b7280;
  --border: #e5e7eb;
}

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

header {
  margin-bottom: 16px;
}

.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
}
```

確認:
- 「今日の勤怠」と「未出勤」が左右に並ぶ
- バッジ表示になる

コード解説:
- `.panel-header`に`display: flex`を使い、子要素を横並びにしている
- `.status-badge`は小要素専用の装飾。情報の種類を色で区別する狙い
- `.panel-header` は `class="panel-header"` を持つ要素だけに適用される
- `display: flex;` は子要素を横並びにするレイアウトモード
- `align-items: center;` は縦方向（上下）を中央にそろえる
- `justify-content: space-between;` は左右に最大限離して配置する
- `display: inline-block;` は `span` に幅・余白を持たせるために使う
- `padding: 4px 10px;` は上下4px、左右10pxの内側余白
- `border-radius: 999px;` は角丸を最大化し、カプセル形状にする定番値
- `background` と `color` はバッジの背景色と文字色
- `font-size: 12px;` はバッジ文字を小さくして補助情報らしくする

#### Step 5: フォーム部品を追加
`styles.css` を次の内容に更新:

```css
:root {
  --bg: #dbeafe;
  --panel: #ffffff;
  --text: #202124;
  --muted: #6b7280;
  --accent: #0ea5e9;
  --border: #e5e7eb;
}

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

header {
  margin-bottom: 16px;
}

.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

input {
  padding: 8px;
  border: 1px solid var(--border);
  border-radius: 6px;
}

button {
  margin-top: 8px;
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
```

確認:
- 入力欄に枠と余白がつく
- ボタンが青くなる

コード解説:
- `label`/`input`/`button`を個別に整えると、フォームの操作性が上がる
- `--accent`を使うことで、ボタン色をテーマとして一元管理できる
- `--accent: #0ea5e9;` は強調色（ボタン色）の変数
- `label { display: flex; flex-direction: column; }` はラベル文字と入力欄を縦に並べる
- `gap: 6px;` はラベル内の文字と入力欄の間隔
- `input` の `padding` は入力しやすさ、`border` は境界線、`border-radius` は角丸
- `button` の `margin-top: 8px;` は入力欄との間隔
- `button` の `color: #fff;` は白文字
- `button` の `border: none;` はデフォルト枠線を消す
- `cursor: pointer;` はマウスを乗せたときにクリック可能カーソルにする

#### Step 6: 最終形（表と共通設定を追加）
`styles.css` を次の内容に更新:

```css
:root {
  --bg: #dbeafe;
  --panel: #ffffff;
  --text: #202124;
  --muted: #6b7280;
  --accent: #0ea5e9;
  --border: #e5e7eb;
}

* { box-sizing: border-box; }

body {
  margin: 0;
  font-family: "Segoe UI", Tahoma, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

header {
  margin-bottom: 16px;
}

.subtitle {
  color: var(--muted);
  margin: 0 0 16px;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

input {
  padding: 8px;
  border: 1px solid var(--border);
  border-radius: 6px;
}

button {
  margin-top: 8px;
  padding: 8px 12px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border-bottom: 1px solid var(--border);
  text-align: left;
  padding: 8px;
}
```

コード解説:
- `* { box-sizing: border-box; }`でサイズ計算を安定化し、崩れを防ぐ
- `table`と`th, td`を追加して、一覧データを読みやすい形に整えている
- `*` は「全要素」を意味するセレクタ
- `box-sizing: border-box;` は「幅・高さに padding/border を含めて計算」する設定
- `table { width: 100%; }` は親要素いっぱいに表を広げる
- `border-collapse: collapse;` はセル間の二重線を1本化する
- `th, td` は「見出しセルとデータセルの両方」に同じ設定を適用する書き方
- `border-bottom` は行の区切り線
- `text-align: left;` は文字を左揃え
- `padding: 8px;` はセル内余白で可読性を上げる

### 1-4. ブラウザで開く
1. エクスプローラーで `index.html` をダブルクリック  
2. 画面が表示されることを確認

### 1-5. ミニ演習A（HTML）
段階作成でできた `index.html` を改造して、タグ理解を固定します。

1. `<header>` を `<div>` に置き換え、見た目差を確認  
2. `<section class="panel">` を `<article class="panel">` に置き換え、見た目差を確認  
3. フォームに `input type="date"` を追加  
4. テーブルに「状態」列（`<th>状態</th>` / `<td>退勤済み</td>`）を追加  
5. `ul` の項目を1つ削除し、表示が1行減ることを確認

### 1-6. ミニ演習B（CSS）
1. 背景色（`--bg`）を好みの色に変更  
2. ボタン色（`--accent`）を変更  
3. `panel` の角丸（`border-radius`）を 12px に変更  
4. `status-badge` の背景色を変更

### 1-7. ミニ制作（30〜45分）
「勤怠トップ画面」を HTML/CSS だけで再現します。

要件:
- 「日付 / 出勤時刻 / 退勤時刻」を表示する
- 「状態バッジ」を表示する
- 「出勤」「退勤」ボタンのダミーを置く
- 一覧（表）を1行だけ用意する

完成したら画面をスクショして保存（提出用）

---

## 2. Java 基礎（午後）
このセクション以降は [day0-java.md](./day0-java.md) を参照してください。

