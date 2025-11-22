package com.devsync.controller;

import com.devsync.model.UserSettings;
import com.devsync.repository.UserSettingsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SettingsController.class)
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetDefaultSettingsForNewUser() throws Exception {
        when(userSettingsRepository.findByUserId("testuser"))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/settings/testuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.maxMethodLength").value(50))
            .andExpect(jsonPath("$.aiProvider").value("ollama"));
    }

    @Test
    void shouldSaveUserSettings() throws Exception {
        UserSettings settings = new UserSettings("testuser");
        settings.setMaxMethodLength(30);
        settings.setAiProvider("openai");

        when(userSettingsRepository.save(any(UserSettings.class)))
            .thenReturn(settings);

        mockMvc.perform(post("/api/settings/testuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(settings)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.maxMethodLength").value(30))
            .andExpect(jsonPath("$.aiProvider").value("openai"));
    }
}