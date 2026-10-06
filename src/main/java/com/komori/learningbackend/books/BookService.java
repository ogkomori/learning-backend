package com.komori.learningbackend.books;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;

	public List<BookEntity> findAll() {
		return bookRepository.findAll();
	}

	public Page<BookEntity> findByPage(Integer page, Integer size) {
		if (page == null || size == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both page and size must be specified");
		}

		PageRequest pageRequest = PageRequest.of(page, size);
        return bookRepository.findAll(pageRequest);
	}

	public BookEntity findById(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
	}
}
