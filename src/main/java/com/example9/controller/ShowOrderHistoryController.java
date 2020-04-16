package com.example9.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example9.domain.Order;
import com.example9.domain.User;
import com.example9.form.SerchHistoryForm;
import com.example9.service.ShowOrderHistoryService;

/**
 * 注文履歴表示を行うコントローラ.
 * 
 * @author mayumiono
 *
 */
@Controller
@RequestMapping("/order-history")
public class ShowOrderHistoryController {

	@Autowired
	private ShowOrderHistoryService showOrderHistoryService;

	@Autowired
	private HttpSession session;

	@ModelAttribute
	public SerchHistoryForm setUpForm() {
		return new SerchHistoryForm();
	}

	/** 1ページに表示する注文履歴件数 */
	private static final int VIEW_SIZE = 5;
	/** 絞り込み条件の最小日付 */
	public Date minDate = null;
	/** 絞り込み条件の最大日付 */
	public Date maxDate = null;
	/** 本日の日付 */
	public LocalDate today = LocalDate.now();

	/**
	 * 注文履歴一覧の表示.
	 * 
	 * @param form  日付絞り込み情報
	 * @param page  ページ番号
	 * @param model リクエストスコープ
	 * @return 注文履歴一覧画面
	 */
	@RequestMapping("")
	public String showList(SerchHistoryForm form, Integer page, Model model) {

		// セッションスコープからユーザーID取得
		User user = (User) session.getAttribute("user");
		Integer userId = user.getId();

		// -----日付絞り込み機能関連------------------------------------
		complementDateForSearching(form); // 絞り込み日付を歯抜け入力した場合、数値補完をする
		boolean checkDate = checkDateForSerching(form, model); // 入力値に不備がないか確認
		if (!checkDate)	return "order_history"; // 不備ありの場合、一覧へ戻る

		// ページング番号からも検索できるよう、最大・最小日付を保存しておく
		session.setAttribute("minYear", form.getMinYear());
		session.setAttribute("minMonth", form.getMinMonth());
		session.setAttribute("minDay", form.getMinDay());
		session.setAttribute("maxYear", form.getMaxYear());
		session.setAttribute("maxMonth", form.getMaxMonth());
		session.setAttribute("maxDay", form.getMaxDay());

		// -----注文履歴出力------------------------------------
		List<Order> orderList = null;
		try {
			orderList = showOrderHistoryService.getOrderHistoryList(userId, minDate, maxDate);
		} catch (Exception e) {
			// 注文履歴がnullの場合は、その旨のメッセージをリクエストスコープに格納する
			String nonOrderMessage = "注文履歴がありません";
			model.addAttribute("nonOrderMessage", nonOrderMessage);
			return "order_history";
		}

		// -----ページング機能関連------------------------------------
		if (page == null) {
			// ページ数の指定が無い場合は1ページ目を表示させる
			page = 1;
		}

		// 表示させたいページ数、ページサイズ、注文リストを渡し１ページに表示させる注文リストを絞り込み
		Page<Order> orderPage = showOrderHistoryService.showListPaging(page, VIEW_SIZE, orderList);
		model.addAttribute("orderPage", orderPage);

		// ページングのリンクに使うページ数をスコープに格納 (例)28件あり1ページにつき10件表示させる場合→1,2,3がpageNumbersに入る
		List<Integer> pageNumbers = calcPageNumbers(model, orderPage);
		model.addAttribute("pageNumbers", pageNumbers);

		// 注文履歴詳細画面から注文履歴一覧画面に戻る際、元のページに戻れるよう、ページ番号を保存しておく
		session.setAttribute("pageNum", page);

		return "order_history";
	}

	/**
	 * 引数の注文IDを有する注文について、詳細情報を表示する.
	 * 
	 * @param model   リクエストスコープ
	 * @param orderId 注文ID
	 * @return 注文履歴詳細画面
	 */
	@RequestMapping("/detail")
	public String showDetail(Model model, Long orderId) {
		// 注文情報詳細の取得
		List<Order> orderList = showOrderHistoryService.getOrderHistoryDetail(orderId);
		Order order = orderList.get(0);
		model.addAttribute(order);

		return "order_history_detail";
	}

	/**
	 * ページングのリンクに使うページ数をスコープに格納 (例)28件あり1ページにつき10件表示させる場合→1,2,3がpageNumbersに入る
	 * 
	 * @param model     リクエストスコープ
	 * @param orderPage ページング情報
	 * @return ページ番号リスト
	 */
	private List<Integer> calcPageNumbers(Model model, Page<Order> orderPage) {
		int totalPages = orderPage.getTotalPages();
		List<Integer> pageNumbers = null;
		if (totalPages > 0) {
			pageNumbers = new ArrayList<Integer>();
			for (int i = 1; i <= totalPages; i++) {
				pageNumbers.add(i);
			}
		}
		return pageNumbers;
	}

	/**
	 * 絞り込み日付が歯抜け入力された場合、数値補完する.
	 * 
	 * @param form 絞り込み用日付情報
	 */
	public void complementDateForSearching(SerchHistoryForm form) {
		// 絞り込み日付の最小値・最大値を変数格納
		minDate = form.getMinDate();
		maxDate = form.getMaxDate();

		// 絞り込み日付欄の未入力確認（未入力：true, 入力あり：false）
		Boolean minDateIsEmpty = "".equals(form.getMinYear()) && "".equals(form.getMinMonth())
				&& "".equals(form.getMinDay());
		Boolean maxDateIsEmpty = "".equals(form.getMaxYear()) && "".equals(form.getMaxMonth())
				&& "".equals(form.getMaxDay());

		// 最大値のみ入力した場合は、最小値＝西暦始まりとする
		if (minDateIsEmpty && !maxDateIsEmpty) {
			form.setMinYear("0000");
			form.setMinMonth("1");
			form.setMinDay("1");
			minDate = form.getMinDate();
		}

		// 最小値のみ入力した場合は最大値＝本日とする
		if (!minDateIsEmpty && maxDateIsEmpty) {
			form.setMaxYear(String.valueOf(today.getYear()));
			form.setMaxMonth(String.valueOf(today.getMonthValue()));
			form.setMaxDay(String.valueOf(today.getDayOfMonth()));
			maxDate = form.getMaxDate();
		}
	}

	/**
	 * 絞り込み日付の入力チェック.
	 * 
	 * @param form  絞り込み用日付情報
	 * @param model リクエストスコープ
	 * @return チェック結果：不備ありの場合false, 正常値の場合true
	 */
	public boolean checkDateForSerching(SerchHistoryForm form, Model model) {
		// 最小年数・日数：入力済、最小月数：未入力の場合、不整値とみなす
		// 例）2020-01-01：正常値、2020-null-01：不整値
		if (form.getMinYear() != null && !"".equals(form.getMinYear()) && minDate == null) {
			model.addAttribute("dayError", "正しい日付を入力してください");
			return false;
		}

		// 最大年数・日数：入力済、最小月数：未入力の場合、不整値とみなす
		// 例）2020-01-01：正常値、2020-null-01：不整値
		if (form.getMaxYear() != null && !"".equals(form.getMaxYear()) && maxDate == null) {
			model.addAttribute("dayError", "正しい日付を入力してください");
			return false;
		}

		// 最小日付欄への入力値が、本日以降の日付の場合、エラー
		try {
			LocalDate minLocalDate = LocalDate.parse(String.valueOf(minDate), DateTimeFormatter.ISO_DATE);
			if (minLocalDate.compareTo(today) > 0) {
				model.addAttribute("dayError", "左欄には、本日以前の日付を入力してください");
				return false;
			}
		} catch (DateTimeParseException e) {
		}

		// 最小日付よりも最大日付が小さな値の場合、エラー
		if (maxDate != null && minDate != null && maxDate.compareTo(minDate) < 0) {
			model.addAttribute("dayError", "右欄は、左欄以降の日付を入力するか、空欄にしてください");
			return false;
		}

		return true;
	}
}
