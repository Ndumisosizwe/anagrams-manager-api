package com.bsg.anagrams.service;

import com.bsg.anagrams.domain.Word;
import com.bsg.anagrams.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoaderService implements ApplicationRunner {

    private static final int BATCH_SIZE = 1000;
    private final WordRepository wordRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (wordRepository.count() > 0) {
            log.info("Dictionary already loaded — skipping seed.");
            return;
        }

        log.info("Seeding database from Dictionary.txt...");
        long start = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("Dictionary.txt").getInputStream()))) {

            List<Word> batch = new ArrayList<>(BATCH_SIZE);
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    batch.add(new Word(trimmed));
                }
                if (batch.size() == BATCH_SIZE) {
                    wordRepository.saveAll(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                wordRepository.saveAll(batch);
            }
        }

        log.info("Dictionary seeded: {} words in {}ms",
                wordRepository.count(), System.currentTimeMillis() - start);
    }
}
