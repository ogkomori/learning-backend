package com.komori.learningbackend.dto.request;

import java.time.LocalDate;

public record UpdateBookRequest (
        String title,
        String author,
        String isbn,
        LocalDate publishedDate,
        String category
) {
    public boolean isEmpty() {
        return title == null && author == null && isbn == null && category == null && publishedDate == null;
    }
}
