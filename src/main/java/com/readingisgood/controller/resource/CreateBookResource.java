package com.readingisgood.controller.resource;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateBookResource {

	@NotNull(message = "Title field cannot be empty.")
	@Size(min = 1, max = 50, message = "Title field length can be between 1 and 50.")
	private String title;

	@NotNull(message = "Author field cannot be empty.")
	@Size(min = 1, max = 50, message = "Author field length can be between 1 and 50.")
	private String author;

	@NotNull(message = "Stock field cannot be empty.")
	@Min(value = 0, message = "Stock field cannot be less than 0.")
	private Long stock;

	@NotNull(message = "Price field cannot be empty.")
	@Min(value = 0, message = "Price field cannot be less than 0.")
	private BigDecimal price;

}
