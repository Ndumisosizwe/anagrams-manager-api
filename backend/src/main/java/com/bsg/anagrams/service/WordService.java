package com.bsg.anagrams.service;

import com.bsg.anagrams.domain.Word;
import com.bsg.anagrams.dto.WordRequest;
import com.bsg.anagrams.dto.WordResponse;
import com.bsg.anagrams.exception.DuplicateWordException;
import com.bsg.anagrams.exception.WordNotFoundException;
import com.bsg.anagrams.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordService {

    private final WordRepository wordRepository;

    public Page<WordResponse> getAllWords(Pageable pageable) {
        return wordRepository.findAll(pageable).map(WordResponse::from);
    }

    @Transactional
    @CacheEvict(value = "anagramCounts", allEntries = true)
    public WordResponse addWord(WordRequest request) {
        String normalized = request.word().toUpperCase();
        if (wordRepository.existsByWordIgnoreCase(normalized)) {
            throw new DuplicateWordException(normalized);
        }
        Word saved = wordRepository.save(new Word(normalized));
        log.debug("Added word: {}", normalized);
        return WordResponse.from(saved);
    }

    @Transactional
    @CacheEvict(value = "anagramCounts", allEntries = true)
    public void deleteWord(String word) {
        if (!wordRepository.existsByWordIgnoreCase(word)) {
            throw new WordNotFoundException(word);
        }
        wordRepository.deleteByWordIgnoreCase(word);
        log.debug("Deleted word: {}", word.toUpperCase());
    }

    public List<WordResponse> getAnagrams(String word) {
        Word existing = wordRepository.findByWordIgnoreCase(word)
                .orElseThrow(() -> new WordNotFoundException(word));
        return wordRepository.findAnagramsOf(existing.getSortedChars(), existing.getWord())
                .stream()
                .map(WordResponse::from)
                .toList();
    }

    public List<String> getExampleWordsWithAnagrams(int count) {
        return wordRepository.findRandomWordsWithAnagrams(count);
    }
}
