package com.bsg.anagrams.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Anagram counts per word length with total computation time")
public record AnagramCountResponse(

        @Schema(description = "Computation time in milliseconds") long computationTimeMs,
        @Schema(description = "Anagram group counts per word length") List<AnagramCountEntry> results
) {
    @Schema(description = "Anagram count for a specific word length")
    public record AnagramCountEntry(
            @Schema(description = "Word length", example = "6") int wordLength,
            @Schema(description = "Number of anagram groups found", example = "3") long anagramCount,
            @Schema(description = "Human-readable summary") String summary
    ) {
        public static AnagramCountEntry of(int wordLength, long count) {
            return new AnagramCountEntry(
                    wordLength,
                    count,
                    "Words with the character length of " + wordLength + " had " + count + " anagrams"
            );
        }
    }
}
