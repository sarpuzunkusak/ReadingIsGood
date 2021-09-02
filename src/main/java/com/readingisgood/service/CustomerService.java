package com.readingisgood.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.readingisgood.exception.DuplicateEntryException;
import com.readingisgood.repository.CustomerRepository;
import com.readingisgood.repository.entity.Customer;

@Service
public class CustomerService {

	private CustomerRepository customerRepository;

	public CustomerService(CustomerRepository repository) {
		this.customerRepository = repository;
	}

	public Customer createCustomer(Customer customer) throws DuplicateEntryException {
		Optional<Customer> existing = customerRepository.findByEmail(customer.getEmail());

		if (existing.isPresent()) {
			throw new DuplicateEntryException("A customer with the same email already exists.");
		}

		return customerRepository.save(customer);
	}

}
