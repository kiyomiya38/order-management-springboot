package com.shinesoft.attendance.service; // Service層パッケージ

import java.time.LocalDate; // 日付
import java.time.LocalDateTime; // 日時
import java.util.List; // 一覧取得

import org.slf4j.Logger; // ログ出力
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service; // Service登録
import org.springframework.transaction.annotation.Transactional; // 更新処理をトランザクション化

import com.shinesoft.attendance.domain.Attendance; // 勤怠エンティティ
import com.shinesoft.attendance.domain.AttendanceStatus; // 勤怠状態Enum
import com.shinesoft.attendance.domain.User; // ユーザーエンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.repository.AttendanceRepository; // 勤怠DBアクセス
import com.shinesoft.attendance.repository.UserRepository; // ユーザーDBアクセス

@Service
public class AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class); // ロガー

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public Attendance getTodayAttendance(Long userId) { // 当日勤怠取得（無ければnull）
        return attendanceRepository.findByUser_IdAndWorkDate(userId, LocalDate.now())
                .orElse(null);
    }

    public Attendance getAttendance(Long id) { // ID指定取得（管理者編集で使用）
        return attendanceRepository.findWithUserById(id)
            .orElseThrow(() -> new BusinessException("勤怠が存在しません"));
    }

    public List<Attendance> listAttendances(Long userId) { // ユーザー本人向け一覧
        return attendanceRepository.findByUser_IdOrderByWorkDateDesc(userId);
    }

    public List<Attendance> listAllAttendances() { // 管理者向け全件一覧
        return attendanceRepository.findAllByOrderByWorkDateDesc();
    }

    @Transactional
    public Attendance clockIn(Long userId) { // 出勤処理
        LocalDate today = LocalDate.now();
        Attendance existing = attendanceRepository.findByUser_IdAndWorkDate(userId, today).orElse(null);
        if (existing != null) {
            throw new BusinessException("すでに出勤済みです");
        }

        User user = getUser(userId);
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        attendance.setStartTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.WORKING);

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Clock in userId={}, date={}, time={}", userId, today, saved.getStartTime());
        return saved;
    }

    @Transactional
    public Attendance clockOut(Long userId) { // 退勤処理
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUser_IdAndWorkDate(userId, today)
                .orElseThrow(() -> new BusinessException("退勤するには先に出勤してください"));

        if (attendance.getStatus() == AttendanceStatus.FINISHED) {
            throw new BusinessException("すでに退勤済みです");
        }
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください");
        }

        attendance.setEndTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.FINISHED);
        Attendance saved = attendanceRepository.save(attendance);
        log.info("Clock out userId={}, date={}, time={}", userId, today, saved.getEndTime());
        return saved;
    }

    @Transactional
    public Attendance updateAttendance(Long attendanceId, // 管理者編集処理
                                       Long userId,
                                       LocalDate workDate,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime,
                                       AttendanceStatus status) {
        if (userId == null || workDate == null || status == null) {
            throw new BusinessException("ユーザー、勤務日、状態は必須です");
        }

        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new BusinessException("勤怠が存在しません"));

        User user = getUser(userId); // getUserの戻り値はUser型

        Attendance existing = attendanceRepository.findByUser_IdAndWorkDate(userId, workDate).orElse(null); // 同日重複チェック
        if (existing != null && !existing.getId().equals(attendanceId)) {
            throw new BusinessException("同じ日付の勤怠が既に存在します");
        }

        validateStatusAndTimes(workDate, status, startTime, endTime); // 状態と時刻の整合チェック

        attendance.setUser(user);
        attendance.setWorkDate(workDate);
        attendance.setStartTime(startTime);
        attendance.setEndTime(endTime);
        attendance.setStatus(status);
        return attendanceRepository.save(attendance);
    }

    private User getUser(Long userId) { // 共通ユーザー取得
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("ユーザーが存在しません"));
    }

    private void validateStatusAndTimes(LocalDate workDate,
                                        AttendanceStatus status, // 整合性ルール
                                        LocalDateTime startTime,
                                        LocalDateTime endTime) {
        // Java-06で学習したif / else ifを使い、状態ごとの入力ルールを確認する
        if (status == AttendanceStatus.NOT_STARTED) {
            if (startTime != null || endTime != null) {
                throw new BusinessException("未出勤の時刻は空にしてください");
            }
        } else if (status == AttendanceStatus.WORKING) {
            if (startTime == null || endTime != null) {
                throw new BusinessException("出勤中は開始時刻のみ必要です");
            }
            if (!startTime.toLocalDate().equals(workDate)) {
                throw new BusinessException("開始時刻の日付は勤務日と一致させてください");
            }
        } else if (status == AttendanceStatus.FINISHED) {
            if (startTime == null || endTime == null) {
                throw new BusinessException("退勤済みは開始・終了時刻が必要です");
            }
            if (!startTime.toLocalDate().equals(workDate)) {
                throw new BusinessException("開始時刻の日付は勤務日と一致させてください");
            }
            if (endTime.isBefore(startTime)) {
                throw new BusinessException("終了時刻は開始時刻以降にしてください");
            }
        }
    }
}