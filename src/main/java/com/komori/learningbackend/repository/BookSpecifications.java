package com.komori.learningbackend.repository;

import com.komori.learningbackend.entity.BookEntity;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecifications {
    public static Specification<BookEntity> titleContains(String query) {
        return ((root, _, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + query.toLowerCase() + "%"
                )
        );
    }

    public static Specification<BookEntity> categoryEquals(String query) {
        return ((root, _, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")),
                        query.toLowerCase()
                )
        );
    }
}
