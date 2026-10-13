package com.komori.learningbackend.dto.response;

import com.komori.learningbackend.entity.BookEntity;

import java.util.List;

public record BookSearchResponse (
        List<BookEntity> books,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {}
