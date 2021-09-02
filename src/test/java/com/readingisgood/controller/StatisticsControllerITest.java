package com.readingisgood.controller;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Calendar;

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
import com.readingisgood.controller.resource.GetStatisticsResource;
import com.readingisgood.repository.BookRepository;
import com.readingisgood.repository.CustomerRepository;
import com.readingisgood.repository.OrderRepository;
import com.readingisgood.repository.StatisticsRepository;
import com.readingisgood.repository.entity.Book;
import com.readingisgood.repository.entity.Customer;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class StatisticsControllerITest {

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

	@Transactional
	@Test
	public void shouldReturnStatistics() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		mockMvc.perform(MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content("{\"customerId\": \"" + customer.getId() + "\",\"books\": [\"" + book.getId() + "\"]}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.get("/statistics/customer/{customerId}", customer.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String responseBody = result.getResponse().getContentAsString();
		GetStatisticsResource statistics = objectMapper.readValue(responseBody, GetStatisticsResource.class);

		Assertions.assertNotNull(statistics);
		Assertions.assertNotNull(statistics.getStatistics());
		Assertions.assertFalse(statistics.getStatistics().isEmpty());
		Assertions.assertEquals(customer.getId(), statistics.getStatistics().get(0).getCustomerId());
		Assertions.assertEquals(1, statistics.getStatistics().get(0).getBookCount());
		Assertions.assertEquals(Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1),
				statistics.getStatistics().get(0).getMonth());
		Assertions.assertEquals(1, statistics.getStatistics().get(0).getOrderCount());
		Assertions.assertTrue(new BigDecimal(30.5d).compareTo(statistics.getStatistics().get(0).getTotalAmount()) == 0);

	}

	@Transactional
	@Test
	public void shouldReturnAllStatistics() throws Exception {

		Customer customer = new Customer("email1", "customer1", "address1");
		customerRepository.save(customer);

		Book book = new Book("title", "author", 5, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		mockMvc.perform(MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content("{\"customerId\": \"" + customer.getId() + "\",\"books\": [\"" + book.getId() + "\"]}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
				.content("{\"customerId\": \"" + customer.getId() + "\",\"books\": [\"" + book.getId() + "\"]}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.get("/statistics/customer/{customerId}", customer.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String responseBody = result.getResponse().getContentAsString();
		GetStatisticsResource statistics = objectMapper.readValue(responseBody, GetStatisticsResource.class);

		Assertions.assertNotNull(statistics);
		Assertions.assertNotNull(statistics.getStatistics());
		Assertions.assertFalse(statistics.getStatistics().isEmpty());
		Assertions.assertEquals(customer.getId(), statistics.getStatistics().get(0).getCustomerId());
		Assertions.assertEquals(2, statistics.getStatistics().get(0).getBookCount());
		Assertions.assertEquals(Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1),
				statistics.getStatistics().get(0).getMonth());
		Assertions.assertEquals(2, statistics.getStatistics().get(0).getOrderCount());
		Assertions.assertTrue(new BigDecimal(61.0d).compareTo(statistics.getStatistics().get(0).getTotalAmount()) == 0);

	}

}
