package com.readingisgood.controller.resource;

import java.util.List;

import com.readingisgood.repository.entity.Statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetStatisticsResource {

	private List<Statistics> statistics;

}
