package com.devsync.controller;

import com.devsync.model.User;
import com.devsync.repository.UserRepository;
import com.devsync.repository.AnalysisHistoryRepository;
import com.devsync.repository.AdminSettingsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerUserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AnalysisHistoryRepository analysisHistoryRepository;
    
    @MockitoBean
    private AdminSettingsRepository adminSettingsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetUserDetails() throws Exception {
        User user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(analysisHistoryRepository.findByUserIdOrderByAnalysisDateDesc("1"))
            .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/admin/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, String> updates = Map.of("username", "newusername");

        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
            .andExpect(status().isOk());
    }
}