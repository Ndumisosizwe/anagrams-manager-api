package com.bsg.anagrams.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for adding a new word")
public record WordRequest(

        @NotBlank(message = "Word must not be blank")
        @Size(min = 1, max = 100, message = "Word length must be between 1 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Word must contain only alphabetic characters")
        @Schema(description = "The word to add", example = "SPARE")
        String word
) {}
