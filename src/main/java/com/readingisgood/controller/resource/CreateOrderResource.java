package com.readingisgood.controller.resource;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateOrderResource {

	@NotNull(message = "customerId field cannot be empty.")
	private String customerId;
	@NotNull(message = "Books field cannot be empty.")
	@Size(min = 1, message = "At least 1 book must be ordered.")
	private List<String> books;

}
