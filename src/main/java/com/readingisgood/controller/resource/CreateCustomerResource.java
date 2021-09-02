package com.readingisgood.controller.resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateCustomerResource {

	@NotNull(message = "Email field cannot be empty.")
	@Size(min = 1, max = 100, message = "Email field length can be between 1 and 100.")
	private String email;

	@NotNull(message = "Name field cannot be empty.")
	@Size(min = 1, max = 50, message = "Name field length can be between 1 and 50.")
	private String name;

	@NotNull(message = "Address field cannot be empty.")
	@Size(min = 1, max = 255, message = "Address field length can be between 1 and 255.")
	private String address;

}
