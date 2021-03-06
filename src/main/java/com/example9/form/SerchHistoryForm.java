package com.example9.form;

import java.sql.Date;

/**
 * 注文履歴の絞り込み情報を表すフォーム.
 * 
 * @author mayumiono
 *
 */
public class SerchHistoryForm {

	/** 絞り込み範囲の最小年数 */
	private String minYear;
	/** 絞り込み範囲の最小月数 */
	private String minMonth;
	/** 絞り込み範囲の最小日数 */
	private String minDay;
	/** 絞り込み範囲の最大年数 */
	private String maxYear;
	/** 絞り込み範囲の最大月数 */
	private String maxMonth;
	/** 絞り込み範囲の最大日数 */
	private String maxDay;

	/**
	 * 最小年・月・日数を組み合わせ、最小年月日を作成.
	 * 
	 * @return 最小年月日(不整値入力時はnull)
	 */
	public Date getMinDate() {
		// 年数入力ありにもかかわらず月数未入力の場合、デフォルトで1月を設定
		if (!"".equals(minYear) && "".equals(minMonth) && "".equals(minDay)) {
			minMonth = "1";
		}
		// 年数・月数入力ありにもかかわらず日数未入力の場合、デフォルトで1日を設定
		if ((!"".equals(minYear) && !"".equals(minMonth)) && "".equals(minDay)) {
			minDay = "1";
		}
		try {
			String date = minYear + "-" + minMonth + "-" + minDay;
			Date minDate = Date.valueOf(date);
			return minDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 最大年・月・日数を組み合わせ、最大年月日を作成.
	 * 
	 * @return 最大年月日
	 */
	public Date getMaxDate() {
		// 年数入力ありにもかかわらず月数未入力の場合、デフォルトで12月を設定
		if (!"".equals(maxYear) && "".equals(maxMonth) && "".equals(maxDay)) {
			maxMonth = "12";
		}
		// 年数・月数入力ありにもかかわらず日数未入力の場合、デフォルトで31日を設定
		if ((!"".equals(maxYear) && !"".equals(maxMonth)) && "".equals(maxDay)) {
			maxDay = "31";
		}
		String date = maxYear + "-" + maxMonth + "-" + maxDay;
		try {
			Date maxDate = Date.valueOf(date);
			return maxDate;
		} catch (Exception e) {
			return null;
		}
	}

	public String getMinYear() {
		return minYear;
	}

	public void setMinYear(String minYear) {
		this.minYear = minYear;
	}

	public String getMinMonth() {
		return minMonth;
	}

	public void setMinMonth(String minMonth) {
		this.minMonth = minMonth;
	}

	public String getMinDay() {
		return minDay;
	}

	public void setMinDay(String minDay) {
		this.minDay = minDay;
	}

	public String getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(String maxYear) {
		this.maxYear = maxYear;
	}

	public String getMaxMonth() {
		return maxMonth;
	}

	public void setMaxMonth(String maxMonth) {
		this.maxMonth = maxMonth;
	}

	public String getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(String maxDay) {
		this.maxDay = maxDay;
	}

	@Override
	public String toString() {
		return "SerchHistoryForm [minYear=" + minYear + ", minMonth=" + minMonth + ", minDay=" + minDay + ", maxYear="
				+ maxYear + ", maxMonth=" + maxMonth + ", maxDay=" + maxDay + "]";
	}

}
