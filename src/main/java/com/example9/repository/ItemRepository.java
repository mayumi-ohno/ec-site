package com.example9.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example9.domain.Item;

/**
 * 検索を行うリポジトリ―クラスです.
 * 
 * @author mizuki.tanimori
 *
 */
@Repository
public class ItemRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	/** Itemオブジェクトを生成するローマッパー */
	public static final RowMapper<Item> ITEM_ROW_MAPPER = (rs, i) -> {
		Item item = new Item();
		item.setId(rs.getInt("id"));
		item.setName(rs.getString("name"));
		item.setDescription(rs.getString("description"));
		item.setPriceM(rs.getInt("price_m"));
		item.setPriceL(rs.getInt("price_l"));
		item.setImagePath(rs.getString("image_path"));
		item.setDeleted(rs.getBoolean("deleted"));
		// double型の計算では誤差が生じるためBigDecimal型で数値を扱う
		BigDecimal aveEvaluation = BigDecimal.valueOf(rs.getDouble("ave_evaluation"));
		aveEvaluation = aveEvaluation.setScale(1, BigDecimal.ROUND_HALF_EVEN);
		item.setAveEvaluation(Double.parseDouble(String.valueOf(aveEvaluation.toString())));
		item.setCountEvaluation(rs.getInt("count_evaluation"));
		return item;
	};

	/**
	 * 全件検索を行います.（金額昇順）
	 * 
	 * @return Itemリスト
	 */
	public List<Item> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.id,A.name,A.description,A.description,A.price_m, ");
		sql.append(
				"A.price_l,A.image_path,A.deleted,B.ave_evaluation AS ave_evaluation,B.count_evaluation AS count_evaluation ");
		sql.append("FROM items AS A LEFT OUTER JOIN ");
		sql.append("( SELECT AVG(evaluation)AS ave_evaluation, COUNT(evaluation)AS count_evaluation, item_id ");
		sql.append("FROM reviews GROUP BY item_id) AS B ");
		sql.append("ON A.id=B.item_id ORDER BY A.price_m;");
		return template.query(sql.toString(), ITEM_ROW_MAPPER);
	}

	/**
	 * 商品名の曖昧検索を行います.（金額昇順）
	 * 
	 * @param name 名前
	 * @return Itemリスト
	 */
	public List<Item> findByLikeName(String name) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.id,A.name,A.description,A.description,A.price_m, ");
		sql.append(
				"A.price_l,A.image_path,A.deleted,B.ave_evaluation AS ave_evaluation,B.count_evaluation AS count_evaluation ");
		sql.append("FROM items AS A LEFT OUTER JOIN ");
		sql.append("( SELECT AVG(evaluation)AS ave_evaluation, COUNT(evaluation)AS count_evaluation, item_id ");
		sql.append("FROM reviews GROUP BY item_id) AS B ");
		sql.append("ON A.id=B.item_id ");
		sql.append("WHERE A.name ILIKE :name ");
		sql.append("ORDER BY A.price_m");
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", "%" + name + "%");
		return template.query(sql.toString(), param, ITEM_ROW_MAPPER);
	}

	/**
	 * 引数の商品IDで商品情報を検索します.
	 * 
	 * @param id 商品ID
	 * @return 商品情報詳細
	 */
	public Item findById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.id, A.name, A.description, A.price_m, A.price_l, A.image_path, A.deleted,");
		sql.append(" B.ave_evaluation, B.count_evaluation");
		sql.append(" FROM items AS A LEFT OUTER JOIN");
		sql.append(" ( SELECT AVG(evaluation) AS ave_evaluation, COUNT(evaluation) AS count_evaluation, item_id");
		sql.append(" FROM reviews GROUP BY item_id) AS B");
		sql.append(" ON A.id=B.item_id WHERE id=:id;");
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		Item item = template.queryForObject(sql.toString(), param, ITEM_ROW_MAPPER);
		return item;
	}

	/**
	 * 検索数を6つに制限したSQL文を発行します.
	 * 
	 * @param number 開始位置
	 * @return 商品一覧
	 */
	public List<Item> findByAllLimit(Integer number) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.id,A.name,A.description,A.description,A.price_m, ");
		sql.append(
				"A.price_l,A.image_path,A.deleted,B.ave_evaluation AS ave_evaluation,B.count_evaluation AS count_evaluation ");
		sql.append("FROM items AS A LEFT OUTER JOIN ");
		sql.append("( SELECT AVG(evaluation)AS ave_evaluation, COUNT(evaluation)AS count_evaluation, item_id ");
		sql.append("FROM reviews GROUP BY item_id) AS B ");
		sql.append("ON A.id=B.item_id ORDER BY A.price_m ");
		sql.append("LIMIT 6 OFFSET " + number);
		SqlParameterSource param = new MapSqlParameterSource().addValue("number", number);
		return template.query(sql.toString(), param, ITEM_ROW_MAPPER);
	}

}
