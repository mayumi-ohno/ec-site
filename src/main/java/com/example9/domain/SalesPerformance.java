package com.example9.domain;

/**
 * 売上実績を表すドメイン.
 * 
 * @author mayumiono
 *
 */
public class SalesPerformance {

	/** 売上年 */
	private Integer year;
	/** 売上月 */
	private Integer month;
	/** 売上額 */
	private Integer amount;

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "salesPerformance [year=" + year + ", month=" + month + ", amount=" + amount + "]";
	}

}
