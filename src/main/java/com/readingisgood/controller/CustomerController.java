package com.readingisgood.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingisgood.controller.resource.CreateCustomerResource;
import com.readingisgood.controller.resource.GetOrderResource;
import com.readingisgood.controller.resource.OrderedBookResource;
import com.readingisgood.exception.ApplicationException;
import com.readingisgood.repository.entity.Customer;
import com.readingisgood.repository.entity.Order;
import com.readingisgood.service.CustomerService;
import com.readingisgood.service.OrderService;

@RequestMapping("/customer")
@RestController
public class CustomerController {

	private CustomerService customerService;

	private OrderService orderService;

	public CustomerController(CustomerService customerService, OrderService orderService) {
		this.customerService = customerService;
		this.orderService = orderService;
	}

	@PreAuthorize("#oauth2.hasScope('customer.write')")
	@PostMapping
	public ResponseEntity<Customer> createCustomer(@RequestBody @Valid CreateCustomerResource request)
			throws ApplicationException {
		Customer newCustomer = customerService
				.createCustomer(new Customer(request.getEmail(), request.getName(), request.getAddress()));
		return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
	}

	@PreAuthorize("#oauth2.hasScope('customer.read')")
	@GetMapping(path = "/{customerId}/orders")
	public ResponseEntity<Page<GetOrderResource>> getAllOrdersByCustomerId(@PathVariable String customerId,
			Pageable pageable) {
		Page<Order> orders = orderService.findAllOrdersByCustomerId(customerId, pageable);
		List<GetOrderResource> collection = orders.stream()
				.map(o -> new GetOrderResource(o.getId(), o.getCustomerId(), o.getCreateDate(),
						o.getBooks().stream().map(b -> new OrderedBookResource(b.getId(), b.getTitle(), b.getAuthor()))
								.collect(Collectors.toSet()),
						o.getStatus()))
				.collect(Collectors.toList());

		return ResponseEntity.ok().body(new PageImpl<>(collection, orders.getPageable(), orders.getTotalElements()));
	}
}
