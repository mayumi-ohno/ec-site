package com.example9.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example9.domain.SalesPerformance;
import com.example9.service.CreateChartService;

/**
 * 売上グラフを表示するコントローラ.
 * 
 * @author mayumiono
 *
 */
@Controller
@RequestMapping("/chart")
public class ChartController {

	@Autowired
	private CreateChartService createChartService;

	/**
	 * グラフを表示する.
	 * 
	 * @param year  表示年度
	 * @param model リクエストスコープ
	 * @return グラフ表示画面
	 */
	@RequestMapping("")
	public String index(String year, Model model) {

		String thisYear = String.valueOf(LocalDate.now().getYear());
		if (Objects.isNull(year) || year.isEmpty()) {
			year = thisYear;
		}
		model.addAttribute("selectedYear", year);

		// 過去５ヵ年の年数リストを作成（セレクトボックス用）
		List<Integer> yearList = new ArrayList<>();
		for (int i = (Integer.parseInt(thisYear) - 5); i <= Integer.parseInt(thisYear); i++) {
			yearList.add(i);
		}
		model.addAttribute("yearList", yearList);

		List<SalesPerformance> salesPerformances = createChartService.getSalesPerformances(year);

		// 当該年の売上無しの場合は、月別金額を1-12月すべて0円とする
		if (Objects.isNull(salesPerformances)) {
			model.addAttribute("amount", "0,0,0,0,0,0,0,0,0,0,0,0");
			return "chart";
		}

		// グラフ作成用に月別金額を文字列として連結する
		StringBuilder salesAmount = new StringBuilder();
		int nextMonthOfList = salesPerformances.get(0).getMonth();
		for (int loopCount = 1, index = 0; loopCount <= 12; loopCount++) {

			if (nextMonthOfList == loopCount) {
				salesAmount.append(salesPerformances.get(index).getAmount() + ",");
				index++;
			} else {
				salesAmount.append("0,");
			}

			if (index < salesPerformances.size()) {
				nextMonthOfList = salesPerformances.get(index).getMonth();
			}
		}

		// 作成した文字列の最後尾”,”を削除し、リクエストスコープに格納
		salesAmount.setLength(salesAmount.length() - 1);
		model.addAttribute("amount", salesAmount);

		return "chart";
	}

}
