package com.komori.learningbackend.books;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
	private final BookService bookService;

	@GetMapping
	public ResponseEntity<?> getAllBooks(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		if (page == null && size == null) {
            List<BookEntity> bookEntities = bookService.findAll();
			return ResponseEntity.ok(bookEntities);
		}

		Page<BookEntity> bookPage = bookService.findByPage(page, size);
		return ResponseEntity.ok(bookPage);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookEntity> getBookById(@PathVariable Long id) {
		BookEntity book = bookService.findById(id);
		return ResponseEntity.ok(book);
	}
}
