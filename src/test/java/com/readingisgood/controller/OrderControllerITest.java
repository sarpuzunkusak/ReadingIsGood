package com.readingisgood.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readingisgood.controller.resource.GetOrderResource;
import com.readingisgood.enumeration.OrderStatus;
import com.readingisgood.repository.BookRepository;
import com.readingisgood.repository.CustomerRepository;
import com.readingisgood.repository.OrderRepository;
import com.readingisgood.repository.StatisticsRepository;
import com.readingisgood.repository.entity.Book;
import com.readingisgood.repository.entity.Customer;
import com.readingisgood.repository.entity.Order;
import com.readingisgood.repository.entity.Statistics;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class OrderControllerITest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Resource
	private CustomerRepository customerRepository;

	@Resource
	private OrderRepository orderRepository;

	@Resource
	private BookRepository bookRepository;

	@Resource
	private StatisticsRepository statisticsRepository;

	@AfterEach
	public void destroy() {
		customerRepository.deleteAll();
		orderRepository.deleteAll();
		bookRepository.deleteAll();
	}

	@Test
	public void shouldCreateOrder() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		mockMvc.perform(MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content("{\"customerId\": \"" + customer.getId() + "\",\"books\": [\"" + book.getId() + "\"]}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		Book current = bookRepository.findById(book.getId()).get();
		Assertions.assertEquals(0, current.getStock());

		List<Statistics> statistics = statisticsRepository.findAll();
		Assertions.assertNotNull(statistics);
		Assertions.assertFalse(statistics.isEmpty());
		Assertions.assertEquals(customer.getId(), statistics.get(0).getCustomerId());
		Assertions.assertEquals(1, statistics.get(0).getBookCount());
		Assertions.assertEquals(Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1), statistics.get(0).getMonth());
		Assertions.assertEquals(1, statistics.get(0).getOrderCount());
		Assertions.assertTrue(new BigDecimal(30.5d).compareTo(statistics.get(0).getTotalAmount()) == 0);

	}

	@Test
	public void shouldReturnBadRequestWhenBookListEmpty() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		mockMvc.perform(MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content("{\"customerId\": \"" + customer.getId() + "\",\"books\": []}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenBookOutOfStock() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 0, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		mockMvc.perform(MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content("{\"customerId\": \"" + customer.getId() + "\",\"books\": [\"" + book.getId() + "\"]}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void shouldReturnBadRequestWhenBookNotAvailable() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		mockMvc.perform(MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content("{\"customerId\": \"" + customer.getId() + "\",\"books\": [\"somebookid\"]}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Transactional
	@Test
	public void shouldReturnOrderWhenIdProvided() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		Order order = new Order(customer.getId(), new HashSet<>(Arrays.asList(book)), OrderStatus.NEW);
		orderRepository.save(order);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderId}", order.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String responseBody = result.getResponse().getContentAsString();
		GetOrderResource responseResource = objectMapper.readValue(responseBody, GetOrderResource.class);

		Assertions.assertNotNull(responseResource);
		Assertions.assertEquals(customer.getId(), responseResource.getCustomerId());
		Assertions.assertEquals(OrderStatus.NEW, responseResource.getStatus());
		Assertions.assertNotNull(responseResource.getBooks());
		Assertions.assertFalse(responseResource.getBooks().isEmpty());
		Assertions.assertEquals(1, responseResource.getBooks().size());
		Assertions.assertEquals(book.getId(), responseResource.getBooks().iterator().next().getId());
	}

	@Transactional
	@Test
	public void shouldReturnBadRequestWhenInvalidIdProvided() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		Order order = new Order(customer.getId(), new HashSet<>(Arrays.asList(book)), OrderStatus.NEW);
		orderRepository.save(order);

		mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderId}", "randomorderid"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Transactional
	@Test
	public void shouldReturnOrdersByDate() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		Order order = new Order(customer.getId(), new HashSet<>(Arrays.asList(book)), OrderStatus.NEW);
		orderRepository.save(order);

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date tommorrow = calendar.getTime();

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.get("/order").queryParam("startDate", format.format(today))
						.queryParam("endDate", format.format(tommorrow)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String responseBody = result.getResponse().getContentAsString();
		GetOrderResource[] responseList = objectMapper.readValue(responseBody, GetOrderResource[].class);

		Assertions.assertNotNull(responseList);
		Assertions.assertEquals(1, responseList.length);

	}

}
