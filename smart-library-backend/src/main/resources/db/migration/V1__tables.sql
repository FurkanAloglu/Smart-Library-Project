CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE authors (
                         id UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
                       id UUID PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       isbn VARCHAR(20) NOT NULL UNIQUE,
                       stock INT NOT NULL,
                       image_url VARCHAR(255),
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE borrowings (
                            id UUID PRIMARY KEY,
                            user_id UUID NOT NULL REFERENCES users(id),
                            book_id UUID NOT NULL REFERENCES books(id),
                            borrow_date TIMESTAMP  NOT NULL,
                            due_date TIMESTAMP  NOT NULL,
                            return_date TIMESTAMP ,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE penalties (
                           id UUID PRIMARY KEY,
                           borrowing_id UUID NOT NULL UNIQUE REFERENCES borrowings(id),
                           amount DECIMAL(10, 2) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);