document.addEventListener("DOMContentLoaded", () => { // HTMLの読み込み完了後にDOM操作を始める
  const searchInput = document.getElementById("user-search-input"); // ユーザー名検索欄を取得する
  const roleSelect = document.getElementById("role-filter-select"); // ロール選択欄を取得する
  const resultText = document.getElementById("user-filter-result"); // 件数表示用のp要素を取得する
  const noMatchRow = document.getElementById("no-match-row"); // 0件メッセージ行を取得する
  const rows = Array.from(document.querySelectorAll("tr.js-user-row")); // ユーザー行をすべて取得し、配列に変換する

  if (!(searchInput instanceof HTMLInputElement) || // 検索欄がinput要素でなければ異常
      !(roleSelect instanceof HTMLSelectElement) || // ロール欄がselect要素でなければ異常
      !(resultText instanceof HTMLElement) || // 件数表示欄がHTML要素でなければ異常
      !(noMatchRow instanceof HTMLTableRowElement) || // 0件行がtr要素でなければ異常
      rows.length === 0) { // ユーザー行が1件も取得できなければ異常
    console.log("必要要素が見つからないため終了"); // 異常時の確認メッセージ
    return; // 以降の処理を実行せずに終了する
  }

  console.log("要素取得OK", { rowCount: rows.length }); // 取得できた行数を表示する
});