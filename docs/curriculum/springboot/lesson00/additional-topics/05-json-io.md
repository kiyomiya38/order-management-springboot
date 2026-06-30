# 05. JSON送受信

位置づけ:
- この資料は参考資料です。
- 本線では `docs/curriculum/javascript/javascript-fetch-json.md` と `docs/curriculum/web-app(簡易版)` で、JSON送受信を実際のJava API連携として扱います。
- Spring Bootでは `docs/curriculum/springboot/lesson06/lesson6.md` で、手書きJSONではなく Jackson によるJSON変換へ進みます。

## 目的
- 画面とサーバー間でJSONを安全に扱えるようにする

## 学ぶこと
- `JSON.stringify`
- `response.json()`
- JSONのキー名設計
- サーバー側のJSON文字列生成とエスケープ

## 完了条件
- フロント/バック双方で同じJSON構造を扱える
- 文字列に日本語や記号が含まれても崩れない

## 対応アプリ
- `step1-greeting-web`
- `step3-kakeibo-lite-web`

## 本線教材との対応
- `JSON.stringify` / `response.json()`: `docs/curriculum/javascript/javascript-fetch-json.md`
- 手書きJSONの学習用実装: `docs/curriculum/web-app(簡易版)/lesson1.md` 以降
- JacksonによるJSON変換: `docs/curriculum/springboot/lesson06/lesson6.md`
