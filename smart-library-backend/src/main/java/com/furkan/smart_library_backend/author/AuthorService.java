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
        Author author = Author.builder()
                .name(request.name())
                .deleted(false)
                .build();
        return AuthorResponse.fromEntity(authorRepository.save(author));
    }

    @Transactional
    public AuthorResponse updateAuthor(UUID id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        author.setName(request.name());
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