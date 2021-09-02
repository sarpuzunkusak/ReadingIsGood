package com.readingisgood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.readingisgood.repository.entity.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, String> {

	List<Customer> findAll();

	Optional<Customer> findByEmail(String email);

}
