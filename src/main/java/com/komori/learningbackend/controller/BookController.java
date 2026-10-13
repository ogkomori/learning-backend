package com.komori.learningbackend.controller;

import com.komori.learningbackend.dto.request.CreateBookRequest;
import com.komori.learningbackend.dto.request.UpdateBookRequest;
import com.komori.learningbackend.dto.response.BookSearchResponse;
import com.komori.learningbackend.entity.BookEntity;
import com.komori.learningbackend.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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

	@PostMapping
	public ResponseEntity<BookEntity> createBook(@Valid @RequestBody CreateBookRequest createBookRequest) {
		BookEntity bookEntity = bookService.createBook(createBookRequest);
		URI location = URI.create("/api/v1/books/" + bookEntity.getId());
		return ResponseEntity
				.created(location)
				.body(bookEntity);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<BookEntity> updateBook(@PathVariable Long id, @RequestBody UpdateBookRequest updateBookRequest) {
		BookEntity bookEntity = bookService.updateBook(id, updateBookRequest);
		return ResponseEntity.ok(bookEntity);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		return ResponseEntity.noContent().build();
	}
}
