package com.readingisgood.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "customer")
public class Customer {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Id
	private String id;

	@Column
	private String email;

	@Column
	private String name;

	@Column
	private String address;

	public Customer(String email, String name, String address) {
		this.email = email;
		this.name = name;
		this.address = address;
	}

}
