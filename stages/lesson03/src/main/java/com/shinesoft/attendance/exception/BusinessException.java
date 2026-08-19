// 業務ルール違反を表す独自例外
// 例: 二重出勤、未出勤退勤など
package com.shinesoft.attendance.exception;

public class BusinessException extends RuntimeException {
    // 画面に表示するメッセージを受け取る
    public BusinessException(String message) {
        super(message);
    }
}