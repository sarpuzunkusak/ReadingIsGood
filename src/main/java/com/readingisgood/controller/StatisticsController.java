package com.readingisgood.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.readingisgood.controller.resource.GetStatisticsResource;
import com.readingisgood.repository.entity.Statistics;
import com.readingisgood.service.StatisticsService;

@RestController
public class StatisticsController {

	private StatisticsService statisticsService;

	public StatisticsController(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}

	@PreAuthorize("#oauth2.hasScope('statistics.read')")
	@GetMapping(path = "/statistics/customer/{customerId}")
	public ResponseEntity<GetStatisticsResource> getCustomerStatistics(@PathVariable String customerId) {
		List<Statistics> statistics = statisticsService.getByCustomerId(customerId);
		return ResponseEntity.ok().body(new GetStatisticsResource(statistics));
	}

}
