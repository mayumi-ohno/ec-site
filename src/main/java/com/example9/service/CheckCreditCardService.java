package com.example9.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example9.domain.CheckedCreditCard;
import com.example9.form.OrderForm;

/**
 * クレジットカード情報の確認をするサービス.
 * 
 * @author mayuiono
 *
 */
@Service
public class CheckCreditCardService {

	@Autowired
	private RestTemplate restTemplate;

	/** WebApiのwarファイルデプロイ先URL */
	private static final String URL = "http://192.168.2.105:8080/credit-card-api/credit-card/payment";

	/**
	 * クレジットカード情報の確認.
	 * 
	 * @param form クレジットカード情報
	 * @return 確認情報
	 */
	public CheckedCreditCard checkCardInfo(OrderForm form) {
		return restTemplate.postForObject(URL, form, CheckedCreditCard.class);
	}
}
