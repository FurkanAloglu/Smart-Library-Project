CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       is_deleted BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE authors (
                         id UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(100) UNIQUE NOT NULL,
                            is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE books (
                       id UUID PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       isbn VARCHAR(50) UNIQUE,
                       stock INT NOT NULL CHECK (stock >= 0),
                       is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE borrowings (
                            id UUID PRIMARY KEY,
                            user_id UUID NOT NULL,
                            book_id UUID NOT NULL,
                            borrowed_at DATE NOT NULL,
                            due_date DATE NOT NULL,
                            returned_at DATE
);

CREATE TABLE penalties (
                           id UUID PRIMARY KEY,
                           borrowing_id UUID UNIQUE NOT NULL,
                           amount NUMERIC(10,2) NOT NULL,
                           is_paid BOOLEAN DEFAULT FALSE,
                           calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
