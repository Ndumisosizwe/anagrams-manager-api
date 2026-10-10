package com.bsg.anagrams.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "All anagram groups for a given word length")
public record AnagramGroupsResponse(

        @Schema(description = "Word length") int wordLength,
        @Schema(description = "Anagram groups — each inner list is a set of words that are anagrams of each other")
        List<List<String>> groups
) {}
