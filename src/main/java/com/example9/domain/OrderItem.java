package com.example9.domain;

import java.util.List;

/**
 * 注文商品を表すドメインクラス.
 * 
 * @author yuuki
 *
 */
public class OrderItem {
	/** 主キー */
	private Integer id;
	/** 商品id */
	private Integer itemId;
	/** 注文id */
	private Long orderId;
	/** 数量 */
	private Integer quantity;
	/** サイズ */
	private Character size;
	/** 商品 */
	private Item item;
	/** 注文トッピングリスト */
	private List<OrderTopping> orderToppingList;
	/** 口コミID */
	private Integer reviewId;

	public Integer getReviewId() {
		return reviewId;
	}

	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Character getSize() {
		return size;
	}

	public void setSize(Character size) {
		this.size = size;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public List<OrderTopping> getOrderToppingList() {
		return orderToppingList;
	}

	public void setOrderToppingList(List<OrderTopping> orderToppingList) {
		this.orderToppingList = orderToppingList;
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", itemId=" + itemId + ", orderId=" + orderId + ", quantity=" + quantity
				+ ", size=" + size + ", item=" + item + ", orderToppingList=" + orderToppingList + ", reviewId="
				+ reviewId + "]";
	}

	/**
	 * 注文商品の小計を求める.
	 * 
	 * @return 小計
	 */
	public int getSubTotal() {
		int subTotalPrice = 0;

		if (orderToppingList.size() == 0 && size == 'M') {
			subTotalPrice = item.getPriceM() * quantity;
		} else if (orderToppingList.size() == 0 && size == 'L') {
			subTotalPrice = item.getPriceL() * quantity;
		} else if (orderToppingList.size() != 0 && size == 'M') {
			subTotalPrice = (item.getPriceM()
					+ orderToppingList.get(0).getTopping().getPriceM() * orderToppingList.size()) * quantity;
		} else if (orderToppingList.size() != 0 && size == 'L') {
			subTotalPrice = (item.getPriceL()
					+ orderToppingList.get(0).getTopping().getPriceL() * orderToppingList.size()) * quantity;
		}
		return subTotalPrice;
	}

}
