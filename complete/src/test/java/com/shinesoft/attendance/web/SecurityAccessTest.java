package com.shinesoft.attendance.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void administrator_cannotDeleteOwnAccount() throws Exception {
        var admin = userRepository.findByUsername("admin").orElseThrow();

        mockMvc.perform(post("/users/{id}/delete", admin.getId()).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/users"));

        assertTrue(userRepository.existsById(admin.getId()));
    }
}
