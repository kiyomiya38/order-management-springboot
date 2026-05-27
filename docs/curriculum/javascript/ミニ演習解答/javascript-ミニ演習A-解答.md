# JavaScript ミニ演習A 解答

対象資料: `docs/curriculum/javascript/javascript.md`

## ミニ演習A（JavaScript）解答

`script.js` の末尾に、次のコードを追記します。

```javascript
// 勤怠データを配列で用意します。
// 1件分のデータは、ユーザー名・勤務状態・勤務時間（分）を持つオブジェクトです。
const attendances = [
  { username: "tanaka", status: "WORKING", minutes: 120 },
  { username: "suzuki", status: "FINISHED", minutes: 480 },
  { username: "sato", status: "WORKING", minutes: 240 }
];

// status が "WORKING" のデータだけを取り出します。
const workingAttendances = attendances.filter((attendance) => {
  return attendance.status === "WORKING";
});

// minutes を合計します。
let totalMinutes = 0;

for (const attendance of attendances) {
  totalMinutes += attendance.minutes;
}

// username だけを取り出して、新しい配列を作ります。
const usernames = attendances.map((attendance) => {
  return attendance.username;
});

// 確認用にコンソールへ出力します。
console.log("WORKING件数:", workingAttendances.length);
console.log("合計分:", totalMinutes);
console.log("名前一覧:", usernames);
```

## 実行結果

ブラウザの開発者ツールの Console に、次のような内容が表示されます。

```text
WORKING件数: 2
合計分: 840
名前一覧: ["tanaka", "suzuki", "sato"]
```

配列の表示形式はブラウザによって少し異なる場合があります。

## 補足: find の確認

特定のユーザーを1件だけ探す場合は、`find` を使います。

```javascript
// username が "tanaka" のデータを1件探します。
const tanakaAttendance = attendances.find((attendance) => {
  return attendance.username === "tanaka";
});

console.log("tanakaの勤怠:", tanakaAttendance);
```

## 確認ポイント

- `filter` で条件に合うデータだけを取り出せている
- `for...of` で配列を順番に処理できている
- `map` で必要な値だけを取り出した配列を作れている
- `console.log` で処理結果を確認できている
