CREATE TABLE book_authors (
                              book_id UUID NOT NULL,
                              author_id UUID NOT NULL,
                              PRIMARY KEY (book_id, author_id),
                              CONSTRAINT fk_book_author_book
                                  FOREIGN KEY (book_id) REFERENCES books(id),
                              CONSTRAINT fk_book_author_author
                                  FOREIGN KEY (author_id) REFERENCES authors(id)
);

CREATE TABLE book_categories (
                                 book_id UUID NOT NULL,
                                 category_id UUID NOT NULL,
                                 PRIMARY KEY (book_id, category_id),
                                 CONSTRAINT fk_book_category_book
                                     FOREIGN KEY (book_id) REFERENCES books(id),
                                 CONSTRAINT fk_book_category_category
                                     FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- BORROWING → USER
ALTER TABLE borrowings
    ADD CONSTRAINT fk_borrowing_user
        FOREIGN KEY (user_id) REFERENCES users(id);

-- BORROWING → BOOK
ALTER TABLE borrowings
    ADD CONSTRAINT fk_borrowing_book
        FOREIGN KEY (book_id) REFERENCES books(id);

-- PENALTY → BORROWING
ALTER TABLE penalties
    ADD CONSTRAINT fk_penalty_borrowing
        FOREIGN KEY (borrowing_id) REFERENCES borrowings(id);
