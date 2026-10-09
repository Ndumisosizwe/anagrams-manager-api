package com.bsg.anagrams.controller;

import com.bsg.anagrams.dto.AnagramCountResponse;
import com.bsg.anagrams.dto.AnagramCountResponse.AnagramCountEntry;
import com.bsg.anagrams.service.AnagramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnagramController.class)
class AnagramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnagramService anagramService;

    @Test
    void getAnagramCounts_returns200WithCorrectStructure() throws Exception {
        when(anagramService.computeAnagramCounts()).thenReturn(
                new AnagramCountResponse(42L, List.of(
                        AnagramCountEntry.of(4, 5L),
                        AnagramCountEntry.of(6, 3L)
                ))
        );

        mockMvc.perform(get("/api/anagrams/counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.computationTimeMs").value(42))
                .andExpect(jsonPath("$.results[0].wordLength").value(4))
                .andExpect(jsonPath("$.results[0].anagramCount").value(5))
                .andExpect(jsonPath("$.results[0].summary").value("Words with the character length of 4 had 5 anagrams"))
                .andExpect(jsonPath("$.results[1].wordLength").value(6))
                .andExpect(jsonPath("$.results[1].summary").value("Words with the character length of 6 had 3 anagrams"));
    }
}
