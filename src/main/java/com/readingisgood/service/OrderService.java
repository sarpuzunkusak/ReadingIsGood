package com.readingisgood.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.readingisgood.enumeration.OrderStatus;
import com.readingisgood.exception.RecordNotFoundException;
import com.readingisgood.repository.OrderRepository;
import com.readingisgood.repository.entity.Book;
import com.readingisgood.repository.entity.Order;

@Service
public class OrderService {

	private OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public Page<Order> findAllOrdersByCustomerId(String customerId, Pageable page) {
		return orderRepository.findAllByCustomerId(customerId, page);
	}

	public Order createOrder(String customerId, Set<Book> books) {
		Order order = new Order(customerId, books, OrderStatus.NEW);
		return orderRepository.save(order);
	}

	public Order getOrderById(String orderId) throws RecordNotFoundException {
		Optional<Order> optional = orderRepository.findById(orderId);

		if (!optional.isPresent()) {
			throw new RecordNotFoundException("Order cannot be found.");
		}

		return optional.get();
	}

	public List<Order> getOrdersByDateInterval(Date startDate, Date endDate) {
		return orderRepository.findAllByCreateDateBetween(startDate, endDate);
	}

}
