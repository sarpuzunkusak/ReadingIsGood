package com.readingisgood.repository.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.GenericGenerator;

import com.readingisgood.enumeration.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "order_")
public class Order {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Id
	private String id;

	@Column(name = "customer_id")
	private String customerId;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Book.class)
	@JoinTable(name = "order_books", joinColumns = { @JoinColumn(referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(referencedColumnName = "id") })
	private Set<Book> books;

	@Column(name = "status")
	private OrderStatus status;

	@Column(name = "create_date")
	private Date createDate;

	public Order(String customerId, Set<Book> books, OrderStatus status) {
		this.customerId = customerId;
		this.books = books;
		this.status = status;
		this.createDate = Calendar.getInstance().getTime();
	}

}
