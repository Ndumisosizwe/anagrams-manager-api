package com.bsg.anagrams.controller;

import com.bsg.anagrams.dto.AnagramCountResponse;
import com.bsg.anagrams.service.AnagramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anagrams")
@RequiredArgsConstructor
@Tag(name = "Anagrams", description = "Anagram analysis operations")
public class AnagramController {

    private final AnagramService anagramService;

    @GetMapping("/counts")
    @Operation(
            summary = "Get anagram counts per word length",
            description = "Returns the number of anagram groups for each word length in the dictionary, " +
                          "along with the total computation time in milliseconds. " +
                          "Result is cached and invalidated automatically on any word add/delete.")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<AnagramCountResponse> getAnagramCounts() {
        return ResponseEntity.ok(anagramService.computeAnagramCounts());
    }
}
