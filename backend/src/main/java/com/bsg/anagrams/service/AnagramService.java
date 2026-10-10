package com.bsg.anagrams.service;

import com.bsg.anagrams.dto.AnagramCountResponse;
import com.bsg.anagrams.dto.AnagramCountResponse.AnagramCountEntry;
import com.bsg.anagrams.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
