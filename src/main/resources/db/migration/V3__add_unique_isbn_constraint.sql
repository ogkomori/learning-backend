ALTER TABLE books
    ADD CONSTRAINT uc_books_isbn UNIQUE (isbn);