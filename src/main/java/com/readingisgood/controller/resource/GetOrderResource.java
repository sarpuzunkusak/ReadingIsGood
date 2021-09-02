package com.readingisgood.controller.resource;

import java.util.Date;
import java.util.Set;

import com.readingisgood.enumeration.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetOrderResource {

	private String id;
	private String customerId;
	private Date createDate;
	private Set<OrderedBookResource> books;
	private OrderStatus status;

}
