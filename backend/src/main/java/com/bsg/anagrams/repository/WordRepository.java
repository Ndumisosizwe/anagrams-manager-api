package com.bsg.anagrams.repository;

import com.bsg.anagrams.domain.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByWordIgnoreCase(String word);

    boolean existsByWordIgnoreCase(String word);

    void deleteByWordIgnoreCase(String word);

    List<Word> findBySortedCharsAndWordNotIgnoreCase(String sortedChars, String word);

    Page<Word> findAll(Pageable pageable);

    @Query("SELECT w.wordLength, COUNT(DISTINCT w.sortedChars) FROM Word w GROUP BY w.wordLength ORDER BY w.wordLength")
    List<Object[]> countAnagramGroupsByWordLength();

    @Query("SELECT w.sortedChars, w.word FROM Word w WHERE w.wordLength = :wordLength ORDER BY w.sortedChars, w.word")
    List<Object[]> findWordsGroupedByLength(@Param("wordLength") int wordLength);

    @Query(value = """
            SELECT w.word FROM words w
            WHERE w.sorted_chars IN (
                SELECT sorted_chars FROM words GROUP BY sorted_chars HAVING COUNT(*) > 1
            )
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<String> findRandomWordsWithAnagrams(@Param("limit") int limit);

    @Query("SELECT w FROM Word w WHERE w.sortedChars = :sortedChars AND UPPER(w.word) != UPPER(:word)")
    List<Word> findAnagramsOf(@Param("sortedChars") String sortedChars, @Param("word") String word);
}
