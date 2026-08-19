// Serviceクラスを置くパッケージ
package com.shinesoft.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

// 業務ロジックを担当するクラス（Controllerから分離）
@Service
public class AttendanceService {
    // 操作履歴を出力するロガー
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    // DBアクセス層（依存注入）
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // コンストラクタインジェクション
    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    // 当日の勤怠を取得（無ければOptional.empty）
    public Optional<Attendance> findToday(Long userId) {
        return attendanceRepository.findByUser_IdAndWorkDate(userId, LocalDate.now());
    }

    // 出勤処理（Lesson2から継続）
    public Attendance clockIn(Long userId) {
        // 1. 今日の日付を取得
        LocalDate today = LocalDate.now();
        // 2. 同日レコードが既にあるか確認
        Optional<Attendance> existing = attendanceRepository.findByUser_IdAndWorkDate(userId, today);
        if (existing.isPresent()) {
            // 3. 既存レコードがあれば二重出勤として業務エラー
            throw new BusinessException("すでに出勤済みです");
        }

        // 4. 対象ユーザー取得（存在しなければシステムエラー）
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("研修ユーザーが存在しません"));

        // 5. 新規勤怠レコード作成
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        // 出勤時刻は現在時刻
        attendance.setStartTime(LocalDateTime.now());
        // 出勤後の状態
        attendance.setStatus(AttendanceStatus.WORKING);

        // 6. 保存してログ出力
        Attendance saved = attendanceRepository.save(attendance);
        log.info("clock-in userId={} date={} time={}", userId, saved.getWorkDate(), saved.getStartTime());
        return saved;
    }

    // 退勤処理（Lesson3で追加）
    public Attendance clockOut(Long userId) {
        // 1. 今日の日付で当日レコードを取得
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUser_IdAndWorkDate(userId, today)
            // レコードが無い = まだ出勤していない
            .orElseThrow(() -> new BusinessException("退勤するには先に出勤してください"));

        // 2. すでに退勤済みなら再退勤は禁止
        if (attendance.getStatus() == AttendanceStatus.FINISHED) {
            throw new BusinessException("すでに退勤済みです");
        }
        // 3. 出勤中以外の状態では退勤不可
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください");
        }

        // 4. 退勤時刻と状態を更新
        attendance.setEndTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.FINISHED);

        // 5. 保存してログ出力
        Attendance saved = attendanceRepository.save(attendance);
        log.info("clock-out userId={} date={} time={}", userId, saved.getWorkDate(), saved.getEndTime());
        return saved;
    }
}