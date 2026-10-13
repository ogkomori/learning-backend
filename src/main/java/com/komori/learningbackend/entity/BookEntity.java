package com.komori.learningbackend.entity;

import com.komori.learningbackend.dto.request.CreateBookRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String author;
	@Column(unique = true)
	private String isbn;
	private LocalDate publishedDate;
	private String category;

	public BookEntity(CreateBookRequest createBookRequest) {
		this.title = createBookRequest.title();
		this.author = createBookRequest.author();
		this.isbn = createBookRequest.isbn();
		this.publishedDate = createBookRequest.publishedDate();
		this.category = createBookRequest.category();
	}
}
