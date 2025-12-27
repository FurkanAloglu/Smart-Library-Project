package com.furkan.smart_library_backend.author;

import com.furkan.smart_library_backend.author.dto.AuthorRequest;
import com.furkan.smart_library_backend.author.dto.AuthorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorResponse> getAllAuthors() {
        try {
            return authorRepository.findAllByDeletedFalse().stream()
                    .map(AuthorResponse::fromEntity)
                    .toList();
        } catch (Exception e) {
            log.error("Error in AuthorService.getAllAuthors", e);
            throw new RuntimeException("Failed to get authors", e);
        }
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        try {
            String cleanName = request.name().trim();

            if (authorRepository.existsByNameIgnoreCaseAndDeletedFalse(cleanName)) {
                throw new IllegalArgumentException("Bu yazar sistemde zaten kayıtlı!");
            }

            Author author = Author.builder()
                    .name(cleanName)
                    .deleted(false)
                    .build();
            return AuthorResponse.fromEntity(authorRepository.save(author));
        } catch (Exception e) {
            log.error("Error in AuthorService.createAuthor request={}", request, e);
            throw new RuntimeException("Failed to create author", e);
        }
    }

    @Transactional
    public AuthorResponse updateAuthor(UUID id, AuthorRequest request) {
        try {
            Author author = authorRepository.findById(id)
                    .filter(a -> !a.isDeleted())
                    .orElseThrow(() -> new EntityNotFoundException("Author not found"));

            String newName = request.name().trim();

            if (!author.getName().equalsIgnoreCase(newName) &&
                    authorRepository.existsByNameIgnoreCaseAndDeletedFalse(newName)) {
                throw new IllegalArgumentException("Bu yazar adı zaten sistemde mevcut!");
            }

            author.setName(newName);
            return AuthorResponse.fromEntity(authorRepository.save(author));
        } catch (Exception e) {
            log.error("Error in AuthorService.updateAuthor id={}", id, e);
            throw new RuntimeException("Failed to update author", e);
        }
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        try {
            Author author = authorRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Author not found"));
            author.setDeleted(true);
            authorRepository.save(author);
        } catch (Exception e) {
            log.error("Error in AuthorService.deleteAuthor id={}", id, e);
            throw new RuntimeException("Failed to delete author", e);
        }
    }
}