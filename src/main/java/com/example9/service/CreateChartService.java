package com.example9.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example9.domain.SalesPerformance;
import com.example9.repository.OrderRepository;

/**
 * グラフ用データを取得するサービス.
 * 
 * @author mayumiono
 *
 */
@Service
@Transactional
public class CreateChartService {

	@Autowired
	private OrderRepository orderRepository;

	/**
	 * 月別売上実績を取得する.
	 * 
	 * @param year 年
	 * @return 月別売上実績
	 */
	public List<SalesPerformance> getSalesPerformances(String year) {
		return orderRepository.getSalesPerformanceByYear(year);
	}

}
