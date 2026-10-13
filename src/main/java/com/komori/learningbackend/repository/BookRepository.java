package com.komori.learningbackend.repository;

import com.komori.learningbackend.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {
    Optional<BookEntity> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);
}
