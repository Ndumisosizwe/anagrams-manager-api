package com.bsg.anagrams.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "words", indexes = {
        @Index(name = "idx_word_value", columnList = "word", unique = true),
        @Index(name = "idx_word_length", columnList = "word_length"),
        @Index(name = "idx_word_length_sorted_chars", columnList = "word_length, sorted_chars")
})
@Getter
@Setter
@NoArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false, unique = true, length = 100)
    private String word;

    @Column(name = "word_length", nullable = false)
    private int wordLength;

    // sorted characters of the word — used as the anagram grouping key
    @Column(name = "sorted_chars", nullable = false, length = 100)
    private String sortedChars;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Word(String word) {
        this.word = word.toUpperCase();
        this.wordLength = word.length();
        this.sortedChars = sortChars(word);
        this.createdAt = LocalDateTime.now();
    }

    private static String sortChars(String word) {
        char[] chars = word.toUpperCase().toCharArray();
        java.util.Arrays.sort(chars);
        return new String(chars);
    }
}
