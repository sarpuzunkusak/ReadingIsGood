package com.readingisgood.repository.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "book")
public class Book {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Id
	private String id;

	@Column
	private String title;

	@Column
	private String author;

	@Column
	private long stock;

	@Column
	private BigDecimal price;

	@Version
	private int version;

	public Book(String title, String author, long stock, BigDecimal price) {
		this.title = title;
		this.author = author;
		this.stock = stock;
		this.price = price;
	}

}
