package com.komori.learningbackend.dto;

import com.komori.learningbackend.entity.BookEntity;

import java.util.List;

public record BookSearchResponse(
        List<BookEntity> books,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {}
