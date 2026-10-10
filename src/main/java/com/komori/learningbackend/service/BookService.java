package com.komori.learningbackend.service;

import com.komori.learningbackend.entity.BookEntity;
import com.komori.learningbackend.repository.BookRepository;
import com.komori.learningbackend.dto.BookSearchResponse;
import com.komori.learningbackend.repository.BookSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
	private static final int MAX_PAGE_SIZE = 100;
	private static final int MAX_RESULTS = 100;
	private static final List<String> VALID_SORT_PARAMETERS = List.of(
			"title", "author", "publisheddate", "category"
	);
	private final BookRepository bookRepository;

	public BookEntity findById(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
	}

	public BookSearchResponse findByParameters(Integer page, Integer size, String query, String category, String sort, String direction) {
		boolean paging = true;
		boolean spec = true;
		PageRequest pageRequest = null;
		Specification<BookEntity> specification = null;

		// PAGING/SORTING VALIDATION
		if (page == null && size == null) {
			paging = false;
		} else if (page == null || size == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both page and size must be specified");
		} else {
			if (page < 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be non-negative");
			}
			if (size <= 0 || size > MAX_PAGE_SIZE) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be between 1 and " + MAX_PAGE_SIZE+  " inclusive");
			}

			if (sort != null) {
				if (!VALID_SORT_PARAMETERS.contains(sort.toLowerCase())) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sort must be one of " + String.join(", ", VALID_SORT_PARAMETERS));
				}
				if (direction == null || direction.equalsIgnoreCase("asc")) {
					pageRequest = PageRequest.of(page, size, Sort.by(sort).ascending());
				} else if (direction.equalsIgnoreCase("desc")) {
					pageRequest = PageRequest.of(page, size, Sort.by(sort).descending());
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Direction must be one of 'asc' or 'desc'");
				}
			} else {
				if (direction == null) {
					pageRequest = PageRequest.of(page, size);
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sort must be specified with direction");
				}
			}
		}

		// FILTERING VALIDATION
		if (query != null && !query.isBlank() && category != null && !category.isBlank()) {
			specification = BookSpecifications.categoryEquals(category).and(BookSpecifications.titleContains(query));
		} else if (query != null && !query.isBlank()) {
			specification = BookSpecifications.titleContains(query);
		} else if (category != null && !category.isBlank()) {
			specification = BookSpecifications.categoryEquals(category);
		} else {
			spec = false;
		}

		if (paging && spec) {
			Page<BookEntity> bookEntityPage = bookRepository.findAll(specification, pageRequest);
			return new BookSearchResponse(bookEntityPage.getContent(), bookEntityPage.getNumber(), bookEntityPage.getSize(), bookEntityPage.getTotalElements(), bookEntityPage.getTotalPages());
		} else if (paging) {
			Page<BookEntity> bookEntityPage = bookRepository.findAll(pageRequest);
			return new BookSearchResponse(bookEntityPage.getContent(), bookEntityPage.getNumber(), bookEntityPage.getSize(), bookEntityPage.getTotalElements(), bookEntityPage.getTotalPages());
		} else if (spec) {
			// MAX_RESULTS here is still not very efficient; to be fixed later with a proper DB limit
			List<BookEntity> list = bookRepository.findAll(specification).stream().limit(MAX_RESULTS).toList();
			return new BookSearchResponse(list, null, null, null, null);
		} else {
			List<BookEntity> list = bookRepository.findAll().stream().limit(MAX_RESULTS).toList();
			return new BookSearchResponse(list, null, null, null, null);
		}
	}
}
