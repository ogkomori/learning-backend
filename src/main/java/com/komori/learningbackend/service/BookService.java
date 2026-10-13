package com.komori.learningbackend.service;

import com.komori.learningbackend.dto.request.CreateBookRequest;
import com.komori.learningbackend.dto.request.UpdateBookRequest;
import com.komori.learningbackend.dto.response.BookSearchResponse;
import com.komori.learningbackend.entity.BookEntity;
import com.komori.learningbackend.repository.BookRepository;
import com.komori.learningbackend.repository.BookSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

	public BookEntity createBook(CreateBookRequest createBookRequest) {
		if (bookRepository.existsByIsbn(createBookRequest.isbn())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book with ISBN " + createBookRequest.isbn() + " already exists");
		}
		BookEntity bookEntity = new BookEntity(createBookRequest);
		return bookRepository.save(bookEntity);
	}

	public BookEntity updateBook(Long id, UpdateBookRequest updateBookRequest) {
		if (updateBookRequest.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in book update request details");
		}

		if (updateBookRequest.title() != null) {
			if (updateBookRequest.title().isBlank()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must be non-blank if supplied");
			}
		}
		if (updateBookRequest.author() != null) {
			if (updateBookRequest.author().isBlank()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author must be non-blank if supplied");
			}
		}
		if (updateBookRequest.category() != null) {
			if (updateBookRequest.category().isBlank()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category must be non-blank if supplied");
			}
		}
		if (updateBookRequest.isbn() != null) {
			if (updateBookRequest.isbn().isBlank()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN must be non-blank if supplied");
			}
		}
		if (updateBookRequest.publishedDate() != null) {
			if (updateBookRequest.publishedDate().isAfter(LocalDate.now())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PublishedDate cannot be in the future");
			}
		}

		Optional<BookEntity> bookEntityOptional = bookRepository.findById(id);
		if (bookEntityOptional.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book (id = " + id + ") not found");
		}

		BookEntity bookEntity = bookEntityOptional.get();

		if (updateBookRequest.title() != null) {
			bookEntity.setTitle(updateBookRequest.title());
		}
		if (updateBookRequest.author() != null) {
			bookEntity.setAuthor(updateBookRequest.author());
		}
		if (updateBookRequest.category() != null) {
			bookEntity.setCategory(updateBookRequest.category());
		}
		if (updateBookRequest.publishedDate() != null) {
			bookEntity.setPublishedDate(updateBookRequest.publishedDate());
		}
		if (updateBookRequest.isbn() != null) {
			Optional<BookEntity> optionalBook = bookRepository.findByIsbn(updateBookRequest.isbn());
			if (optionalBook.isPresent() && !optionalBook.get().getIsbn().equals(bookEntity.getIsbn())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN " + updateBookRequest.isbn() + " already exists");
			} else {
				bookEntity.setIsbn(updateBookRequest.isbn());
			}
		}

		return bookRepository.save(bookEntity);
	}

	public void deleteBook(Long id) {
		Optional<BookEntity> optionalBook = bookRepository.findById(id);
		if (optionalBook.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id " + id + " not found");
		}

		bookRepository.delete(optionalBook.get());
	}
}
