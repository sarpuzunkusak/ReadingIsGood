package com.readingisgood.repository;

import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingisgood.repository.entity.Statistics;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, String> {

	Optional<Statistics> findByCustomerIdAndMonth(String customerId, Month month);

	List<Statistics> findByCustomerId(String customerId);

}
