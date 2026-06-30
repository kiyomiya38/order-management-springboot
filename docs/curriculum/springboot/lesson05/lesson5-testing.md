# Lesson5 必修追加テスト（Service削除制約 / Security認可）

この資料は [Lesson5C テスト・プロファイル・参照整合性](./lesson5c-testing-operations.md) から参照する必修追加テストです。

## 目的

- 勤怠履歴があるユーザーを削除できないことを自動確認する
- 未認証、一般ユーザー、管理者のアクセス差をMockMvcで自動確認する
- ブラウザによる手動確認だけで認証・認可の回帰を判断しない

## 1. テスト用依存関係

`pom.xml` の `<dependencies>` に次があることを確認します。

```xml
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-test</artifactId>
  <scope>test</scope>
</dependency>
```

## 2. `UserServiceTest`を作成

作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/test/java/com/shinesoft/attendance/service/UserServiceTest.java`

```java
package com.shinesoft.attendance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private UserRepository userRepository;
    @Autowired private AttendanceRepository attendanceRepository;

    private User user;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        user = userRepository.findByUsername("user1").orElseThrow();
    }

    @Test
    void create_rejectsInvalidPassword() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.create("short-password-user", "short", "ROLE_USER"));
        assertEquals("パスワードは8〜64文字にしてください", ex.getMessage());
    }

    @Test
    void delete_rejectsUserWithAttendanceHistory() {
        attendanceService.clockIn(user.getId());
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.delete(user.getId()));
        assertEquals("勤怠履歴があるユーザーは削除できません", ex.getMessage());
    }

    @Test
    void delete_removesUserWithoutAttendanceHistory() {
        User created = userService.create("delete-target", "password123", "ROLE_USER");
        userService.delete(created.getId());
        assertFalse(userRepository.existsById(created.getId()));
    }
}
```

## 3. `SecurityAccessTest`を作成

作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/test/java/com/shinesoft/attendance/web/SecurityAccessTest.java`

```java
package com.shinesoft.attendance.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void anonymousUser_isRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void regularUser_cannotOpenUserAdministration() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void administrator_canOpenUserAdministration() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk());
    }
}
```

## 4. 実行と合格条件

```bash
cd ~/order-management-springboot/stages/lesson05
mvn test
```

合格条件:
- `AttendanceServiceTest`: 6件成功
- `UserServiceTest`: 3件成功
- `SecurityAccessTest`: 3件成功
- 合計12件、失敗・エラー・スキップが0件

説明できること:
1. `@WithMockUser` と実際のDBログインの違い
2. 画面に管理者リンクを隠すだけでは認可にならない理由
3. 外部キーエラーになる前にServiceで削除禁止を判定する理由
