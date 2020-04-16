package com.example9.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import com.example9.domain.CheckedCreditCard;
import com.example9.domain.Order;
import com.example9.domain.OrderItem;
import com.example9.domain.OrderTopping;
import com.example9.domain.User;
import com.example9.form.OrderForm;
import com.example9.repository.OrderRepository;

/**
 * 注文処理をするサービス.
 * 
 * @author mayumiono
 *
 */
@Service
@Transactional
public class OrderService {

	@Autowired
	private MailSender sender;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private HttpSession session;

	@Autowired
	private RestTemplate restTemplate;

	/** WebApiのwarファイルデプロイ先URL */
	private static final String URL = "http://192.168.2.105:8080/credit-card-api/credit-card/cancel";

	@ModelAttribute
	public OrderForm setUpOrderForm() {
		return new OrderForm();
	}

	/**
	 * 注文前(status=0)の注文を、注文済(status=1または2)へ更新する.
	 */
	public void doOrder() throws Exception {

		Integer userId = (Integer) session.getAttribute("userId");
		List<Order> orderList = orderRepository.findByUserIdAndStatus(userId, 0);

		// 注文処理-------------------------------------------------------------------------------------------
		for (Order order : orderList) {
			orderRepository.updateStatus(order);
		}
		// ----------------------------------------------------------------------------------------------------

		// 注文メール送信処理-------------------------------------------------------------------------------
		// メールの情報を詰めるオブジェクト
		SimpleMailMessage msg = new SimpleMailMessage();

		// 送信元アドレス
		msg.setFrom("rakurakucurry202001a@gmail.com");

		// ログインユーザの情報を取得
		User user = (User) session.getAttribute("user");
		String email = user.getEmail();
		// 送信先アドレスをセット
		msg.setTo(email);

		// 件名
		msg.setSubject("【ラクラクカリー】ご注文ありがとうございます （自動送信メール）");

		// 本文作成のために必要な注文情報を取得
		Order order = orderList.get(0);

		// 本文を作成
		StringBuilder text = new StringBuilder();
		text.append("\n" + user.getName() + "様" + "\n\n"); // お名前
		// ヘッダー部分
		text.append("この度は「ラクラクカリー」をご利用いただきありがとうございます。\n");
		text.append("お客様のご注文を以下の内容で承りました。\n");
		text.append("ご確認いただき、内容に誤りなどございましたらお知らせください。\n\n");

		// 本文
		text.append("【ご注文内容】" + "\n");

		// 注文内容を表示する
		text.append("--------------------\n");
		for (OrderItem orderItem : order.getOrderItemList()) {
			text.append("（商品名）：" + orderItem.getItem().getName() + "\n");
			text.append("（サイズ）：" + orderItem.getSize() + "\n");
			text.append("（数量）：" + orderItem.getQuantity() + "\n");
			for (OrderTopping orderTopping : orderItem.getOrderToppingList()) {
				text.append("（トッピング）：" + orderTopping.getTopping().getName() + " \n");
			}
			text.append("小計（税抜）：" + String.format("%,d", orderItem.getSubTotal()) + "円\n");
			text.append("--------------------\n");
		}

		text.append("【ご注文番号】" + order.getId() + "\n");
		// Date型をフォーマット
		Date orderDate = order.getOrderDate();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy年M月d日");

		text.append("【ご注文日時】" + dateFormatter.format(orderDate) + "\n");
		text.append("【小計（税抜価格】" + String.format("%,d", order.getCalcTotalPrice()) + "円\n");
		text.append("【消費税】" + String.format("%,d", order.getTax()) + "円\n");
		text.append("【税込価格】" + String.format("%,d", order.getTotalPrice() + order.getTax()) + "円\n\n");

		text.append("--- 注文者情報 ---\n\n");
		text.append("【お名前】" + user.getName() + "\n");
		text.append("【ご住所】" + user.getAddress() + "\n");
		text.append("【電話番号】" + user.getTelephone() + "\n");
		text.append("【メールアドレス】" + user.getEmail() + "\n\n");

		text.append("--- お届け先情報 ---\n\n");
		text.append("【お名前】" + order.getDestinationName() + "\n");
		text.append("【お届け先住所】" + order.getDestinationAddress() + "\n");
		text.append("【電話番号】" + order.getDestinationTel() + "\n");

		// Timestamp型をLocalDateTimeに変換してフォーマットする
		LocalDateTime deliveryTime = order.getDeliveryTime().toLocalDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日HH時");

		text.append("【配送希望日時】" + deliveryTime.format(formatter) + "\n\n"); // deliver_timeの保存できるようになったら追加

		text.append("--- お支払情報 ---\n\n");
		text.append("【お支払い方法】");
		if ("1".equals(order.getPaymentMethod().toString())) {
			text.append("代金引換\n");
		} else if ("2".equals(order.getPaymentMethod().toString())) {
			text.append("クレジットカード\n\n");
		}

		text.append("ご不明な点などがございましたら、お気軽にご連絡ください。\n\n");

		// フッター部分
		text.append("──────────────────────────────────────────\n");
		text.append("ラクラクカリー" + "\n");
		text.append("〒ooo-oooo 東京都oo区" + "\n");
		text.append("メールアドレス： rakurakucurry202001a@gmail.com" + "\n");
		text.append("電話番号：oo-oooo-oooo" + "\n");
		text.append("営業時間：平日 oo:oo 〜 oo:oo  " + "\n");
		text.append("ご注文は24時間受付けております。\nお問合せメールへの返信は2営業日以内にいたします。\n");
		text.append("──────────────────────────────────────────");

		// 上記で作成した本文をセット
		msg.setText(text.toString());

		// 送信処理
		this.sender.send(msg);
		// ----------------------------------------------------------------------------------------------------

	}

	/**
	 * キャンセル処理
	 * 
	 * @return キャンセル成功：true, 失敗：false
	 */
	public boolean cancelOrder() {
		Integer userId = (Integer) session.getAttribute("userId");
		Order order = orderRepository.findByUserIdAndStatus(userId, 0).get(0);
//		String orderId = String.valueOf(order.getId());
		order.setOrder_number("12345"); //動作確認が目的のため適当な数値を入れとく
		CheckedCreditCard result = restTemplate.postForObject(URL, order, CheckedCreditCard.class);
		if ("E-00".equals(result.getError_code())) {
			// WebAPI呼び出し成功したら、注文をキャンセルする
			orderRepository.updateStatus(order);
			return true;
		}
		return false;
	}
}
