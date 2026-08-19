package com.shinesoft.attendance.service; // Serviceテスト用パッケージ

import static org.junit.jupiter.api.Assertions.assertEquals; // 期待値との一致確認
import static org.junit.jupiter.api.Assertions.assertThrows; // 例外発生の確認
import static org.mockito.ArgumentMatchers.any; // 任意の日付をテスト条件に使う
import static org.mockito.ArgumentMatchers.eq; // userIdの一致条件に使う
import static org.mockito.Mockito.when; // Repositoryの戻り値を決める

import java.time.LocalDate; // Repository検索条件の日付型
import java.util.Optional; // 既存勤怠ありを表現する

import org.junit.jupiter.api.BeforeEach; // 各テスト前の準備
import org.junit.jupiter.api.Test; // テストメソッドを示す
import org.junit.jupiter.api.extension.ExtendWith; // MockitoをJUnitで使う
import org.mockito.Mock; // Repositoryの代用品を作る
import org.mockito.junit.jupiter.MockitoExtension; // Mockito初期化を自動化する

import com.shinesoft.attendance.domain.Attendance; // 既存勤怠として返すEntity
import com.shinesoft.attendance.exception.BusinessException; // 確認対象の業務例外
import com.shinesoft.attendance.repository.AttendanceRepository; // テスト用の代用品
import com.shinesoft.attendance.repository.UserRepository; // Service生成に必要な代用品

@ExtendWith(MockitoExtension.class) // Spring Boot全体を起動せずServiceだけ確認する
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository; // DBへ接続しない代用品

    @Mock
    private UserRepository userRepository; // DBへ接続しない代用品

    private AttendanceService attendanceService; // テスト対象

    @BeforeEach
    void setUp() {
        // 本番コードと同じコンストラクタ注入でServiceを作る
        attendanceService = new AttendanceService(attendanceRepository, userRepository);
    }

    @Test
    void clockIn_rejectsSecondClockInOnSameDay() {
        // userId=1の当日勤怠が既に存在する状態を作る
        when(attendanceRepository.findByUser_IdAndWorkDate(eq(1L), any(LocalDate.class)))
            .thenReturn(Optional.of(new Attendance()));

        // 2回目の出勤でBusinessExceptionが発生することを確認する
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> attendanceService.clockIn(1L));

        // 利用者へ返すエラーメッセージも業務仕様として確認する
        assertEquals("すでに出勤済みです", exception.getMessage());
    }
}