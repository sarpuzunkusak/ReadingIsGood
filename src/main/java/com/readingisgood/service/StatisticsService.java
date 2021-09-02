package com.readingisgood.service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.readingisgood.repository.StatisticsRepository;
import com.readingisgood.repository.entity.Book;
import com.readingisgood.repository.entity.Statistics;

@Service
public class StatisticsService {

	private StatisticsRepository statisticsRepository;

	public StatisticsService(StatisticsRepository statisticsRepository) {
		this.statisticsRepository = statisticsRepository;
	}

	public void createOrUpdateCustomerStatistics(String customerId, Set<Book> books) {

		Calendar calendar = Calendar.getInstance();
		Optional<Statistics> existing = statisticsRepository.findByCustomerIdAndMonth(customerId,
				Month.of(calendar.get(Calendar.MONTH) + 1));

		if (existing.isPresent()) {
			Statistics statistics = existing.get();
			statistics.setBookCount(statistics.getBookCount() + books.size());
			statistics.setOrderCount(statistics.getOrderCount() + 1);
			books.stream().forEach(b -> statistics.setTotalAmount(statistics.getTotalAmount().add(b.getPrice())));
			statisticsRepository.save(statistics);
		} else {
			Statistics statistics = new Statistics();
			statistics.setBookCount((long) books.size());
			statistics.setCustomerId(customerId);
			statistics.setMonth(Month.of(calendar.get(Calendar.MONTH) + 1));
			statistics.setOrderCount(1L);
			statistics.setTotalAmount(BigDecimal.ZERO);
			books.stream().forEach(b -> statistics.setTotalAmount(statistics.getTotalAmount().add(b.getPrice())));
			statisticsRepository.save(statistics);
		}
	}

	public List<Statistics> getByCustomerId(String customerId) {
		return statisticsRepository.findByCustomerId(customerId);
	}
}
