package com.readingisgood.repository.entity;

import java.math.BigDecimal;
import java.time.Month;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "statistics")
public class Statistics {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Id
	private String id;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "month")
	private Month month;

	@Column(name = "order_count")
	private Long orderCount;

	@Column(name = "book_count")
	private Long bookCount;

	@Column(name = "total_amount")
	private BigDecimal totalAmount;

}
