package com.bsg.anagrams.service;

import com.bsg.anagrams.domain.Word;
import com.bsg.anagrams.dto.AnagramCountResponse;
import com.bsg.anagrams.repository.WordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnagramServiceTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private AnagramService anagramService;

    @Test
    void computeAnagramCounts_returnsCorrectResults() {
        when(wordRepository.countAnagramGroupsByWordLength()).thenReturn(List.of(
                new Object[]{4, 5L},
                new Object[]{6, 3L}
        ));

        AnagramCountResponse response = anagramService.computeAnagramCounts();

        assertThat(response.results()).hasSize(2);
        assertThat(response.results().get(0).wordLength()).isEqualTo(4);
        assertThat(response.results().get(0).anagramCount()).isEqualTo(5L);
        assertThat(response.results().get(1).wordLength()).isEqualTo(6);
        assertThat(response.results().get(1).anagramCount()).isEqualTo(3L);
        assertThat(response.computationTimeMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void computeAnagramCounts_emptyDictionary_returnsEmptyResults() {
        when(wordRepository.countAnagramGroupsByWordLength()).thenReturn(List.of());

        AnagramCountResponse response = anagramService.computeAnagramCounts();

        assertThat(response.results()).isEmpty();
    }

    @Test
    void anagramCountEntry_summaryFormat_isCorrect() {
        var entry = AnagramCountResponse.AnagramCountEntry.of(5, 3L);
        assertThat(entry.summary()).isEqualTo("Words with the character length of 5 had 3 anagrams");
    }

    @Test
    void word_sortedChars_matchesAnagramPairs() {
        Word spare = new Word("SPARE");
        Word reaps = new Word("REAPS");
        Word pares = new Word("PARES");

        assertThat(spare.getSortedChars()).isEqualTo(reaps.getSortedChars());
        assertThat(spare.getSortedChars()).isEqualTo(pares.getSortedChars());
        assertThat(spare.getSortedChars()).isEqualTo("AEPRS");
    }

    @Test
    void word_nonAnagrams_haveDifferentSortedChars() {
        Word spare = new Word("SPARE");
        Word world = new Word("WORLD");
        assertThat(spare.getSortedChars()).isNotEqualTo(world.getSortedChars());
    }
}
