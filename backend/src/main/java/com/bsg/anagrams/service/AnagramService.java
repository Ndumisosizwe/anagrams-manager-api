package com.bsg.anagrams.service;

import com.bsg.anagrams.dto.AnagramCountResponse;
import com.bsg.anagrams.dto.AnagramCountResponse.AnagramCountEntry;
import com.bsg.anagrams.dto.AnagramGroupsResponse;
import com.bsg.anagrams.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnagramService {

    private final WordRepository wordRepository;

    /**
     * Groups words by sorted-character signature per word length.
     * A group with >1 word represents an anagram set; the count reported
     * is the number of such groups, not the total number of anagram words.
     */
    @Cacheable("anagramCounts")
    public AnagramCountResponse computeAnagramCounts() {
        long start = System.currentTimeMillis();

        List<AnagramCountEntry> results = wordRepository.countAnagramGroupsByWordLength()
                .stream()
                .map(row -> AnagramCountEntry.of((int) (Integer) row[0], (Long) row[1]))
                .toList();

        long elapsed = System.currentTimeMillis() - start;
        log.debug("Anagram counts computed in {}ms", elapsed);
        return new AnagramCountResponse(elapsed, results);
    }

    public AnagramGroupsResponse getGroupsForLength(int wordLength) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();

        for (Object[] row : wordRepository.findWordsGroupedByLength(wordLength)) {
            String key  = (String) row[0];
            String word = (String) row[1];
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }

        // Only return groups that are actual anagram sets (2+ words)
        List<List<String>> groups = grouped.values().stream()
                .filter(g -> g.size() > 1)
                .toList();

        return new AnagramGroupsResponse(wordLength, groups);
    }
}
