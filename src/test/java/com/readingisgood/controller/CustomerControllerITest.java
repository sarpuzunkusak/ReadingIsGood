package com.readingisgood.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.readingisgood.enumeration.OrderStatus;
import com.readingisgood.repository.BookRepository;
import com.readingisgood.repository.CustomerRepository;
import com.readingisgood.repository.OrderRepository;
import com.readingisgood.repository.entity.Book;
import com.readingisgood.repository.entity.Customer;
import com.readingisgood.repository.entity.Order;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class CustomerControllerITest {

	@Autowired
	private MockMvc mockMvc;

	@Resource
	private CustomerRepository customerRepository;

	@Resource
	private OrderRepository orderRepository;

	@Resource
	private BookRepository bookRepository;

	@AfterEach
	public void destroy() {
		customerRepository.deleteAll();
		orderRepository.deleteAll();
		bookRepository.deleteAll();
	}

	@Test
	public void shouldCreateCustomer() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/customer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"osman\",\"address\": \"nereden nereye\",\"email\": \"email\"}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void shouldReturnBadRequestWhenNameMissing() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/customer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"address\": \"nereden nereye\",\"email\": \"email\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenAddressMissing() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/customer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"osman\",\"email\": \"email\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenEmailMissing() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/customer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"osman\",\"address\": \"nereden nereye\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

		Customer customer = new Customer("email", "customer1", "address1");
		customerRepository.save(customer);

		mockMvc.perform(MockMvcRequestBuilders.post("/customer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"osman\",\"address\": \"nereden nereye\",\"email\": \"email\"}"))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

	@Transactional
	@Test
	public void shouldReturnAllOrdersOfCustomer() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		Order order = new Order(customer.getId(), new HashSet<>(Arrays.asList(book)), OrderStatus.NEW);
		orderRepository.save(order);

		mockMvc.perform(MockMvcRequestBuilders.get("/customer/{customerId}/orders", customer.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", Matchers.equalToObject(1)));

	}

}
