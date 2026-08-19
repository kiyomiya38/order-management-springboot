// 勤怠状態を固定値で管理する列挙型（Enum）
// 文字列の打ち間違いを防ぎ、状態の取り得る値を明確にする
package com.shinesoft.attendance.domain;

public enum AttendanceStatus {
    // まだ出勤していない
    NOT_STARTED,
    // 出勤中（開始時刻あり、終了時刻なし）
    WORKING,
    // 退勤済み（開始時刻・終了時刻あり）
    FINISHED
}