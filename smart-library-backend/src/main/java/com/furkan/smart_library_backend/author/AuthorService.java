package com.furkan.smart_library_backend.author;

import com.furkan.smart_library_backend.author.dto.AuthorRequest;
import com.furkan.smart_library_backend.author.dto.AuthorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAllByDeletedFalse().stream()
                .map(AuthorResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        String cleanName = request.name().trim();

        if (authorRepository.existsByNameIgnoreCaseAndDeletedFalse(cleanName)) {
            throw new IllegalArgumentException("Bu yazar sistemde zaten kayıtlı!");
        }

        Author author = Author.builder()
                .name(cleanName)
                .deleted(false)
                .build();
        return AuthorResponse.fromEntity(authorRepository.save(author));
    }

    @Transactional
    public AuthorResponse updateAuthor(UUID id, AuthorRequest request) {
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
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        author.setDeleted(true);
        authorRepository.save(author);
    }
}