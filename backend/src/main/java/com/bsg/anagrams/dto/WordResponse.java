package com.bsg.anagrams.dto;

import com.bsg.anagrams.domain.Word;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "A single word entry")
public record WordResponse(

        @Schema(description = "Internal ID") Long id,
        @Schema(description = "The word value", example = "SPARE") String word,
        @Schema(description = "Character length of the word", example = "6") int wordLength,
        @Schema(description = "Timestamp when this word was added") LocalDateTime createdAt
) {
    public static WordResponse from(Word w) {
        return new WordResponse(w.getId(), w.getWord(), w.getWordLength(), w.getCreatedAt());
    }
}
