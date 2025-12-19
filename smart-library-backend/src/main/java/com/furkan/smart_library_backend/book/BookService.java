package com.furkan.smart_library_backend.book;

import com.furkan.smart_library_backend.author.Author;
import com.furkan.smart_library_backend.author.AuthorRepository;
import com.furkan.smart_library_backend.book.dto.BookRequest;
import com.furkan.smart_library_backend.book.dto.BookResponse;
import com.furkan.smart_library_backend.category.Category;
import com.furkan.smart_library_backend.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAllByDeletedFalse().stream()
                .map(BookResponse::fromEntity)
                .toList();
    }

    public BookResponse getBookById(UUID id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return BookResponse.fromEntity(book);
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByIsbnAndDeletedFalse(request.isbn())) {
            throw new IllegalArgumentException("Book with this ISBN already exists");
        }

        Set<Author> authors = fetchAuthors(request.authorIds());
        Set<Category> categories = fetchCategories(request.categoryIds());

        Book book = Book.builder()
                .title(request.title())
                .description(request.description())
                .isbn(request.isbn())
                .stock(request.stock())
                .imageUrl(request.imageUrl())
                .authors(authors)
                .categories(categories)
                .deleted(false)
                .build();

        return BookResponse.fromEntity(bookRepository.save(book));
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookRequest request) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (!book.getIsbn().equals(request.isbn()) && bookRepository.existsByIsbnAndDeletedFalse(request.isbn())) {
            throw new IllegalArgumentException("ISBN already in use");
        }

        Set<Author> authors = fetchAuthors(request.authorIds());
        Set<Category> categories = fetchCategories(request.categoryIds());

        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setIsbn(request.isbn());
        book.setStock(request.stock());
        book.setImageUrl(request.imageUrl());
        book.setAuthors(authors);
        book.setCategories(categories);

        return BookResponse.fromEntity(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(UUID id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        book.setDeleted(true);
        bookRepository.save(book);
    }

    private Set<Author> fetchAuthors(Set<UUID> ids) {
        List<Author> found = authorRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new EntityNotFoundException("Some authors were not found");
        }
        return new HashSet<>(found);
    }

    private Set<Category> fetchCategories(Set<UUID> ids) {
        List<Category> found = categoryRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new EntityNotFoundException("Some categories were not found");
        }
        return new HashSet<>(found);
    }
}