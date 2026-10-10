package com.komori.learningbackend.books;

import java.util.List;

public record BookSearchResponse(
        List<BookEntity> books,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
)

{}
