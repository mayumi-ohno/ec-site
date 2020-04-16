package com.example9.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example9.service.OrderService;

/**
 * 注文処理をするコントローラ.
 * 
 * @author mayumiono
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	/**
	 * 注文する.
	 * 
	 * @param request クライアントからのリクエスト情報
	 * @return 注文完了画面へのリダイレクト（不正アクセスの場合は404エラー画面）
	 */
	@RequestMapping("")
	public String order(HttpServletRequest request) {

		// 不正な画面遷移で当パスに辿り着いた場合、エラーとする
		String url = request.getHeader("REFERER");
		if (!"http://localhost:8080/confirm".equals(url)
				&& !"http://localhost:8080/confirm/orderAfterConfirm".equals(url)
				&& !"http://localhost:8080/login/after_login".equals(url)) {
			return "/error/404";
		}

		// 注文する
		try {
			orderService.doOrder();
		} catch (Exception e) {
			boolean orderCanceled = orderService.cancelOrder();
			if (orderCanceled) {
				System.out.println("キャンセルしました");
			}
		}
		return "redirect:/order/order-finished";
	}

	/**
	 * 注文完了画面を表示する.
	 * 
	 * @return 注文完了画面
	 */
	@RequestMapping("/order-finished")
	public String orderFinished() {
		return "order_finished";
	}

}
