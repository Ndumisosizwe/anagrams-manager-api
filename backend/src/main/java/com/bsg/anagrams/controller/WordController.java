package com.bsg.anagrams.controller;

import com.bsg.anagrams.dto.PagedResponse;
import com.bsg.anagrams.dto.WordRequest;
import com.bsg.anagrams.dto.WordResponse;
import com.bsg.anagrams.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
@Tag(name = "Words", description = "Word management operations")
public class WordController {

    private final WordService wordService;

    @GetMapping
    @Operation(summary = "Retrieve all words", description = "Returns a paginated, sorted list of all words in the dictionary.")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<PagedResponse<WordResponse>> getAllWords(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "word") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(PagedResponse.from(wordService.getAllWords(PageRequest.of(page, size, sort))));
    }

    @PostMapping
    @Operation(summary = "Add a new word")
    @ApiResponse(responseCode = "201", description = "Word created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "Word already exists")
    public ResponseEntity<WordResponse> addWord(@Valid @RequestBody WordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordService.addWord(request));
    }

    @DeleteMapping("/{word}")
    @Operation(summary = "Delete a word")
    @ApiResponse(responseCode = "204", description = "Word deleted")
    @ApiResponse(responseCode = "404", description = "Word not found")
    public ResponseEntity<Void> deleteWord(
            @Parameter(description = "The word to delete", example = "LISTEN") @PathVariable String word) {
        wordService.deleteWord(word);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{word}/anagrams")
    @Operation(summary = "Get anagrams for a word", description = "Returns all words in the dictionary that are anagrams of the given word.")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Word not found in dictionary")
    public ResponseEntity<List<WordResponse>> getAnagrams(
            @Parameter(description = "The word to find anagrams for", example = "LISTEN") @PathVariable String word) {
        return ResponseEntity.ok(wordService.getAnagrams(word));
    }
}
