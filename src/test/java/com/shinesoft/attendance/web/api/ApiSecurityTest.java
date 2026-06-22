package com.shinesoft.attendance.web.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private AttendanceRepository attendanceRepository;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
    }

    @Test
    void anonymousRequest_returnsJson401() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void regularUser_returnsJson403ForAdminApi() throws Exception {
        mockMvc.perform(get("/api/users").with(httpBasic("user1", "password")))
            .andExpect(status().isForbidden())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void administrator_canReadUsers() throws Exception {
        mockMvc.perform(get("/api/users").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk());
    }

    @Test
    void invalidUserRequest_returnsJson400() throws Exception {
        mockMvc.perform(post("/api/users")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"\",\"password\":\"short\",\"role\":\"ROLE_USER\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void clockIn_usesAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/attendances/clock-in")
                .with(httpBasic("user1", "password")))
            .andExpect(status().isOk());

        var user = userRepository.findByUsername("user1").orElseThrow();
        assertTrue(attendanceRepository
            .findByUser_IdAndWorkDate(user.getId(), LocalDate.now())
            .isPresent());
    }
}
