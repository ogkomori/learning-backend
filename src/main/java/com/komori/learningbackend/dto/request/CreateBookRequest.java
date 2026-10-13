package com.komori.learningbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookRequest (
        @NotBlank String title,
        @NotBlank String author,
        @NotBlank String isbn,
        @NotNull LocalDate publishedDate,
        @NotBlank String category
) {}
