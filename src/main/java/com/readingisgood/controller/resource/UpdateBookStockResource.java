package com.readingisgood.controller.resource;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateBookStockResource {

	@NotNull(message = "Stock field cannot be empty.")
	@Min(value = 0, message = "Stock field cannot be less than 0.")
	private Long stock;

}
