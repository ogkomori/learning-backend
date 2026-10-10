package com.komori.learningbackend.controller;

import com.komori.learningbackend.dto.BookSearchResponse;
import com.komori.learningbackend.entity.BookEntity;
import com.komori.learningbackend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
	private final BookService bookService;

	@GetMapping
	public ResponseEntity<BookSearchResponse> getAllBooks(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(required = false) String query,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String sort,
			@RequestParam(required = false) String direction
	) {
		BookSearchResponse response = bookService.findByParameters(
				page, size, query, category, sort, direction
		);

		System.out.println(response.getClass().getName());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookEntity> getBookById(@PathVariable Long id) {
		BookEntity book = bookService.findById(id);
		return ResponseEntity.ok(book);
	}
}
