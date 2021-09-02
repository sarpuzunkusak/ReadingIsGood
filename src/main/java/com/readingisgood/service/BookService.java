package com.readingisgood.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.readingisgood.exception.AlreadyUpdatedException;
import com.readingisgood.exception.RecordNotFoundException;
import com.readingisgood.repository.BookRepository;
import com.readingisgood.repository.entity.Book;

@Service
public class BookService {

	private BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public Book createBook(Book book) {
		return bookRepository.save(book);
	}

	public void updateBookStock(String bookId, Long stock, Integer version)
			throws RecordNotFoundException, AlreadyUpdatedException {
		Optional<Book> optional = bookRepository.findById(bookId);

		if (!optional.isPresent()) {
			throw new RecordNotFoundException("Book not found.");
		}

		Book book = optional.get();

		if (book.getVersion() != version) {
			throw new AlreadyUpdatedException("Book already updated.");
		}

		book.setStock(stock);
		bookRepository.save(book);
	}

	public Set<Book> findAllByIdIn(List<String> books) {
		return bookRepository.findAllByIdIn(books);
	}

}
