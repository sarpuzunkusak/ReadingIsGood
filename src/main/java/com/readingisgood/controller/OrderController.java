package com.readingisgood.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readingisgood.controller.resource.CreateOrderResource;
import com.readingisgood.controller.resource.GetOrderResource;
import com.readingisgood.controller.resource.OrderedBookResource;
import com.readingisgood.exception.ApplicationException;
import com.readingisgood.exception.OutOfStockException;
import com.readingisgood.repository.entity.Book;
import com.readingisgood.repository.entity.Order;
import com.readingisgood.service.BookService;
import com.readingisgood.service.OrderService;
import com.readingisgood.service.StatisticsService;

@RestController
public class OrderController {

	private OrderService orderService;

	private BookService bookService;

	private StatisticsService statisticsService;

	public OrderController(OrderService orderService, BookService bookService, StatisticsService statisticsService) {
		this.orderService = orderService;
		this.bookService = bookService;
		this.statisticsService = statisticsService;
	}

	@Transactional
	@PreAuthorize("#oauth2.hasScope('order.write')")
	@PostMapping(path = "/order")
	public ResponseEntity<?> createOrder(@RequestBody @Valid CreateOrderResource request) throws ApplicationException {
		Set<Book> books = bookService.findAllByIdIn(request.getBooks());
		validateAvailability(request.getBooks().size(), books.size());
		validateStock(books);
		updateStocks(books);
		updateStatistics(request.getCustomerId(), books);
		Order newOrder = orderService.createOrder(request.getCustomerId(), books);
		return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
	}

	private void validateAvailability(int requestedAmount, int existingAmount) throws OutOfStockException {
		if (requestedAmount != existingAmount) {
			throw new OutOfStockException("One or more books are not available anymore.");
		}
	}

	private void validateStock(Set<Book> books) throws OutOfStockException {
		if (books.stream().anyMatch(b -> b.getStock() == 0)) {
			throw new OutOfStockException("One or more books are out of stock.");
		}
	}

	private void updateStocks(Set<Book> books) throws ApplicationException {
		for (Book book : books) {
			bookService.updateBookStock(book.getId(), book.getStock() - 1, book.getVersion());
		}
	}

	private void updateStatistics(String customerId, Set<Book> books) {
		statisticsService.createOrUpdateCustomerStatistics(customerId, books);
	}

	@PreAuthorize("#oauth2.hasScope('order.read')")
	@GetMapping(path = "/order/{orderId}")
	public ResponseEntity<GetOrderResource> getOrderById(@PathVariable String orderId) throws ApplicationException {
		Order order = orderService.getOrderById(orderId);
		Set<OrderedBookResource> books = order.getBooks().stream()
				.map(b -> new OrderedBookResource(b.getId(), b.getTitle(), b.getAuthor())).collect(Collectors.toSet());
		return ResponseEntity.ok().body(new GetOrderResource(order.getId(), order.getCustomerId(),
				order.getCreateDate(), books, order.getStatus()));
	}

	@PreAuthorize("#oauth2.hasScope('order.read')")
	@GetMapping(path = "/order")
	public ResponseEntity<List<GetOrderResource>> getOrdersByDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
		List<Order> orders = orderService.getOrdersByDateInterval(startDate, endDate);

		List<GetOrderResource> list = new ArrayList<>();
		if (orders != null) {
			for (Order order : orders) {
				Set<OrderedBookResource> books = order.getBooks().stream()
						.map(b -> new OrderedBookResource(b.getId(), b.getTitle(), b.getAuthor()))
						.collect(Collectors.toSet());
				GetOrderResource resource = new GetOrderResource(order.getId(), order.getCustomerId(),
						order.getCreateDate(), books, order.getStatus());
				list.add(resource);
			}
		}

		return ResponseEntity.ok().body(list);
	}

}
