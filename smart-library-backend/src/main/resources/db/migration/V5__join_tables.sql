CREATE TABLE book_authors (
                              book_id UUID NOT NULL,
                              author_id UUID NOT NULL,
                              PRIMARY KEY (book_id, author_id),
                              FOREIGN KEY (book_id) REFERENCES books(id),
                              FOREIGN KEY (author_id) REFERENCES authors(id)
);

CREATE TABLE book_categories (
                                 book_id UUID NOT NULL,
                                 category_id UUID NOT NULL,
                                 PRIMARY KEY (book_id, category_id),
                                 FOREIGN KEY (book_id) REFERENCES books(id),
                                 FOREIGN KEY (category_id) REFERENCES categories(id)
);
