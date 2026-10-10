package com.bsg.anagrams.controller;

import com.bsg.anagrams.dto.WordRequest;
import com.bsg.anagrams.dto.WordResponse;
import com.bsg.anagrams.exception.DuplicateWordException;
import com.bsg.anagrams.exception.WordNotFoundException;
import com.bsg.anagrams.service.WordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
@TestPropertySource(properties = "spring.cache.type=none")
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WordService wordService;

    private WordResponse sampleWord(String w) {
        return new WordResponse(1L, w, w.length(), LocalDateTime.now());
    }

    @Test
    void getAllWords_returns200() throws Exception {
        when(wordService.getAllWords(any(Pageable.class))).thenAnswer(inv -> {
            Pageable p = inv.getArgument(0);
            return new PageImpl<>(List.of(sampleWord("SPARE")), p, 1);
        });

        mockMvc.perform(get("/api/words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].word").value("SPARE"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void addWord_validRequest_returns201() throws Exception {
        when(wordService.addWord(any())).thenReturn(sampleWord("SPARE"));

        mockMvc.perform(post("/api/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WordRequest("SPARE"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.word").value("SPARE"));
    }

    @Test
    void addWord_blankWord_returns400() throws Exception {
        mockMvc.perform(post("/api/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WordRequest(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addWord_nonAlpha_returns400() throws Exception {
        mockMvc.perform(post("/api/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WordRequest("ABC123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addWord_duplicate_returns409() throws Exception {
        when(wordService.addWord(any())).thenThrow(new DuplicateWordException("SPARE"));

        mockMvc.perform(post("/api/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WordRequest("SPARE"))))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteWord_exists_returns204() throws Exception {
        doNothing().when(wordService).deleteWord(eq("SPARE"));

        mockMvc.perform(delete("/api/words/SPARE"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWord_notFound_returns404() throws Exception {
        doThrow(new WordNotFoundException("ZZZZ")).when(wordService).deleteWord(eq("ZZZZ"));

        mockMvc.perform(delete("/api/words/ZZZZ"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAnagrams_returns200() throws Exception {
        when(wordService.getAnagrams(eq("SPARE")))
                .thenReturn(List.of(sampleWord("REAPS"), sampleWord("PARES")));

        mockMvc.perform(get("/api/words/SPARE/anagrams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAnagrams_wordNotFound_returns404() throws Exception {
        when(wordService.getAnagrams(eq("ZZZZ"))).thenThrow(new WordNotFoundException("ZZZZ"));

        mockMvc.perform(get("/api/words/ZZZZ/anagrams"))
                .andExpect(status().isNotFound());
    }
}
