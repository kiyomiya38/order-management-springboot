# Day4（7/14）勤怠一覧 + H2確認（Day4コード使用）

## 目的（Day4でできるようになること）
- 一覧画面が表示できる
- DBに記録された勤怠を確認できる

## 使用するコード
`stages/day4` を使う（一覧画面追加）

---

## 1. 起動
```bash
cd stages/day4
mvn spring-boot:run
```

## 2. 一覧確認
```
http://localhost:8080/attendances
```

## 3. H2コンソール（dev）
```
http://localhost:8080/h2-console
```
JDBC URL:
```
jdbc:h2:mem:attendance
```

---

## 4. チェックポイント
- 一覧に履歴が表示される
- H2で `attendances` が確認できる

---

## 5. 時間割目安
- 午前: 一覧画面理解(60分)
- 午後: H2確認(60分)
