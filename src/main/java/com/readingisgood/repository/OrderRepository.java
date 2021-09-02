package com.readingisgood.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.readingisgood.repository.entity.Order;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, String> {

	Page<Order> findAllByCustomerId(String customerId, Pageable page);

	@Query("select o from order_ o where o.createDate >= :startDate and o.createDate <= :endDate")
	List<Order> findAllByCreateDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("select month(o.createDate), o from order_ o where o.customerId = :customerId group by month(o.createDate), o.id")
	List<Object[]> findAllByCustomerId(@Param("customerId") String customerId);

}
