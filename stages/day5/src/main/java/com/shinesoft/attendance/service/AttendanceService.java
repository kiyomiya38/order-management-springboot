package com.shinesoft.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

@Service
public class AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public Attendance getTodayAttendance(Long userId) {
        return attendanceRepository.findByUser_IdAndWorkDate(userId, LocalDate.now())
                .orElse(null);
    }

    public Attendance getAttendance(Long id) {
        return attendanceRepository.findById(id)
            .orElseThrow(() -> new BusinessException("勤怠が存在しません"));
    }

    public List<Attendance> listAttendances(Long userId) {
        return attendanceRepository.findByUser_IdOrderByWorkDateDesc(userId);
    }

    public List<Attendance> listAllAttendances() {
        return attendanceRepository.findAllByOrderByWorkDateDesc();
    }

    @Transactional
    public Attendance clockIn(Long userId) {
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
    public Attendance clockOut(Long userId) {
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
    public Attendance updateAttendance(Long attendanceId,
                                       Long userId,
                                       LocalDate workDate,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime,
                                       AttendanceStatus status) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new BusinessException("勤怠が存在しません"));

        var user = getUser(userId);

        var existing = attendanceRepository.findByUser_IdAndWorkDate(userId, workDate).orElse(null);
        if (existing != null && !existing.getId().equals(attendanceId)) {
            throw new BusinessException("同じ日付の勤怠が既に存在します");
        }

        validateStatusAndTimes(status, startTime, endTime);

        attendance.setUser(user);
        attendance.setWorkDate(workDate);
        attendance.setStartTime(startTime);
        attendance.setEndTime(endTime);
        attendance.setStatus(status);
        return attendanceRepository.save(attendance);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("ユーザーが存在しません"));
    }

    private void validateStatusAndTimes(AttendanceStatus status,
                                        LocalDateTime startTime,
                                        LocalDateTime endTime) {
        switch (status) {
            case NOT_STARTED -> {
                if (startTime != null || endTime != null) {
                    throw new BusinessException("未出勤の時刻は空にしてください");
                }
            }
            case WORKING -> {
                if (startTime == null || endTime != null) {
                    throw new BusinessException("出勤中は開始時刻のみ必要です");
                }
            }
            case FINISHED -> {
                if (startTime == null || endTime == null) {
                    throw new BusinessException("退勤済みは開始・終了時刻が必要です");
                }
            }
            default -> {
            }
        }
    }
}
