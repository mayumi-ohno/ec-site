package com.example9.service;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example9.domain.Order;
import com.example9.repository.OrderRepository;

/**
 * 注文履歴表示を行うサービス.
 * 
 * @author mayumiono
 *
 */
@Service
public class ShowOrderHistoryService {

	@Autowired
	private OrderRepository orderRepository;

	/**
	 * 引数のユーザーIDを用いて注文確定済の注文情報を検索・出力する.
	 * 
	 * @param userId ユーザーID
	 * @return 注文情報一覧
	 */
	public List<Order> getOrderHistoryList(Integer userId, Date minDate, Date maxDate) {
		return orderRepository.findOrderedDateByOrderDateAndUserId(userId, minDate, maxDate);
	}

	/**
	 * 引数の注文IDに該当する注文履歴詳細を取得する .
	 * 
	 * @param orderId 注文ID
	 * @return 注文履歴詳細
	 */
	public List<Order> getOrderHistoryDetail(Long orderId) {
		return orderRepository.findByOrderId(orderId);
	}

	/**
	 * ページング用メソッド.
	 * 
	 * @param page      表示させたいページ数
	 * @param size      １ページに表示させる注文数
	 * @param orderList 絞り込み対象リスト
	 * @return １ページに表示されるサイズ分の注文一覧情報
	 */
	public Page<Order> showListPaging(int page, int size, List<Order> orderList) {
		// 表示させたいページ数を-1しなければうまく動かない
		page--;
		// どの注文情報から表示させるかと言うカウント値
		int startItemCount = page * size;
		// 絞り込んだ後の注文情報リストが入る変数
		List<Order> list;

		if (orderList.size() < startItemCount) {
			// (ありえないが)もし表示させたい注文カウントがサイズよりも大きい場合は空のリストを返す
			list = Collections.emptyList();
		} else {
			// 該当ページに表示させる注文一覧を作成
			int toIndex = Math.min(startItemCount + size, orderList.size());
			list = orderList.subList(startItemCount, toIndex);
		}

		// 上記で作成した該当ページに表示させる注文一覧をページングできる形に変換して返す
		Page<Order> orderPage = new PageImpl<Order>(list, PageRequest.of(page, size), orderList.size());
		return orderPage;
	}

}
