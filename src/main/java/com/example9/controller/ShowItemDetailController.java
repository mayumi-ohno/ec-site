package com.example9.controller;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example9.domain.Item;
import com.example9.domain.Topping;
import com.example9.service.ShowItemDetailService;

/**
 * 商品詳細表示をするコントローラ.
 * 
 * @author mayumiono
 *
 */
@Controller
@RequestMapping("/item-detail")
public class ShowItemDetailController {

	@Autowired
	private ShowItemDetailService showItemDetailService;

	/**
	 * 引数の商品IDに該当する商品の詳細情報を表示する.
	 * 
	 * @param id    商品ID
	 * @param model リクエストスコープ
	 * @return 商品詳細画面
	 */
	@RequestMapping("")
	public String toItemDetail(Model model, Integer id, HttpServletRequest request, HttpServletResponse response) {

		Item item = showItemDetailService.getAnItem(id);
		List<Topping> toppingList = showItemDetailService.getAllToppings();
		model.addAttribute("toppingPriceM", toppingList.get(0).getPriceM());
		model.addAttribute("toppingPriceL", toppingList.get(0).getPriceL());
		item.setToppingList(toppingList);
		model.addAttribute("item", item);

		// cookieに保存されている閲覧済商品IDを取得する
		Cookie[] cookies = request.getCookies();
		String itemIdHistory = "";
		if (!Objects.isNull(cookies)) {
			for (Cookie cookie : cookies) {
				if ("item-id".equals(cookie.getName())) {
					itemIdHistory = cookie.getValue();
					break;
				}
			}
		}
		// cookie上の商品ID履歴が5件に達している場合は、一番古い履歴を削除する
		String[] itemId = itemIdHistory.split("and");
		if (itemId.length == 5) {
			itemIdHistory = itemId[1] + "and" + itemId[2] + "and" + itemId[3] + "and" + itemId[4];
		}
		Cookie cookie;
		// 今回表示する商品のIDをcookieに保存する
		if (itemIdHistory.isEmpty()) {
			cookie = new Cookie("item-id", String.valueOf(id));
		} else {
			cookie = new Cookie("item-id", itemIdHistory + "and" + String.valueOf(id));
		}
		cookie.setMaxAge(31536000); // 最大1年間保存
		cookie.setPath("/");
		response.addCookie(cookie);
		return "item_detail";
	}

}
