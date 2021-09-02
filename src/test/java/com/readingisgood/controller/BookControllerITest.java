package com.readingisgood.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.readingisgood.repository.BookRepository;
import com.readingisgood.repository.entity.Book;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class BookControllerITest {

	@Autowired
	private MockMvc mockMvc;

	@Resource
	private BookRepository bookRepository;

	@AfterEach
	public void destroy() {
		bookRepository.deleteAll();
	}

	@Test
	public void shouldCreateBook() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders.post("/book").contentType(MediaType.APPLICATION_JSON)
						.content("{\r\n"
								+ "    \"title\": \"kitap2\",\r\n"
								+ "    \"author\": \"yazar2\",\r\n"
								+ "    \"stock\": 3,\r\n"
								+ "    \"price\": 34.5\r\n"
								+ "}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void shouldReturnBadRequestWhenTitleMissing() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/book").contentType(MediaType.APPLICATION_JSON)
				.content("{\r\n"
						+ "    \"author\": \"yazar2\",\r\n"
						+ "    \"stock\": 3,\r\n"
						+ "    \"price\": 34.5\r\n"
						+ "}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenAuthorMissing() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/book").contentType(MediaType.APPLICATION_JSON)
				.content("{\r\n"
						+ "    \"title\": \"kitap2\",\r\n"
						+ "    \"stock\": 3,\r\n"
						+ "    \"price\": 34.5\r\n"
						+ "}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenStockLessThan0() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/book").contentType(MediaType.APPLICATION_JSON)
				.content("{\r\n"
						+ "    \"title\": \"kitap2\",\r\n"
						+ "    \"author\": \"yazar2\",\r\n"
						+ "    \"stock\": -2,\r\n"
						+ "    \"price\": 34.5\r\n"
						+ "}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenPriceLessThan0() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders.post("/book").contentType(MediaType.APPLICATION_JSON)
						.content("{\r\n"
								+ "    \"title\": \"kitap2\",\r\n"
								+ "    \"author\": \"yazar2\",\r\n"
								+ "    \"stock\": 3,\r\n"
								+ "    \"price\": -30\r\n"
								+ "}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	


	@Test
	public void shouldUpdateStock() throws Exception {

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		mockMvc.perform(MockMvcRequestBuilders.put("/book/{bookId}", book.getId()).header("ETag", book.getVersion())
				.contentType(MediaType.APPLICATION_JSON).content("{\"stock\": 3}"))
				.andExpect(MockMvcResultMatchers.status().isOk());

		Book current = bookRepository.findById(book.getId()).get();
		Assertions.assertEquals(3, current.getStock());
	}

	@Test
	public void shouldReturnBadRequestWhenETagNotProvided() throws Exception {

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		mockMvc.perform(MockMvcRequestBuilders.put("/book/{bookId}", book.getId())
				.contentType(MediaType.APPLICATION_JSON).content("{\"stock\": 3}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void shouldReturnNotFoundWhenBookIsNotFound() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.put("/book/randombookid").header("ETag", 1)
				.contentType(MediaType.APPLICATION_JSON).content("{\"stock\": 3}"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void shouldReturnConflictWhenBookIsUpdated() throws Exception {

		Book book = new Book("title", "author", 1, BigDecimal.valueOf(30.5));
		bookRepository.saveAndFlush(book);

		mockMvc.perform(MockMvcRequestBuilders.put("/book/{bookId}", book.getId()).header("ETag", 3)
				.contentType(MediaType.APPLICATION_JSON).content("{\"stock\": 3}"))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}
}
