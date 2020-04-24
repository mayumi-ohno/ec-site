package com.example9.batch;

import org.springframework.batch.item.ItemProcessor;

import com.example9.domain.Item;

/**
 * ItemReaderで読み込んだデータを加工するクラス.
 * 
 * @author mayumiono
 *
 */
public class ItemItemProcessor implements ItemProcessor<Item, Item> {
	/**
	 * ItemReaderで読み込んだデータを加工するメソッドです. 
	 */
	@Override
	public Item process(final Item item) throws Exception {
		// 今回、加工処理は何もしない
		return item;
	}

}
