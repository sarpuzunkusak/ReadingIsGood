package com.readingisgood.controller.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderedBookResource {

	private String id;
	private String title;
	private String author;

}