package com.readingisgood.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingisgood.controller.resource.CreateBookResource;
import com.readingisgood.controller.resource.UpdateBookStockResource;
import com.readingisgood.exception.ApplicationException;
import com.readingisgood.repository.entity.Book;
import com.readingisgood.service.BookService;

@RequestMapping("/book")
@RestController
public class BookController {

	private BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@PreAuthorize("#oauth2.hasScope('book.write')")
	@PostMapping
	public ResponseEntity<?> createBook(@RequestBody @Valid CreateBookResource request) {
		Book newBook = bookService
				.createBook(new Book(request.getTitle(), request.getAuthor(), request.getStock(), request.getPrice()));
		return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
	}

	@PreAuthorize("#oauth2.hasScope('book.write')")
	@PutMapping(path = "/{bookId}")
	public ResponseEntity<?> updateBookStock(@RequestHeader(name = "ETag") Integer etag, @PathVariable String bookId,
			@RequestBody @Valid UpdateBookStockResource request) throws ApplicationException {
		bookService.updateBookStock(bookId, request.getStock(), etag);
		return ResponseEntity.ok().build();
	}

}
