package com.example9.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example9.domain.User;

/**
 * ユーザー情報を扱うリポジトリ.
 * 
 * @author mayumiono
 *
 */
@Repository
public class UserRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	private RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setZipcode(rs.getString("zipcode"));
		user.setAddress(rs.getString("address"));
		user.setTelephone(rs.getString("telephone"));
		// 画像情報取らない場合もあり
		try {
			user.setImage(rs.getString("image"));
		} catch (Exception e) {
		}
		return user;
	};

	/**
	 * メールアドレスでユーザ情報を検索・取得する.
	 * 
	 * @param email メールアドレス
	 * @return ユーザー情報(該当なしの場合null)
	 */
	public User findByEmail(String email) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.id, A.name, A.email, A.password, A.zipcode, A.address, A.telephone, B.image ");
		sql.append("FROM users AS A LEFT OUTER JOIN user_images AS B ON A.id=B.user_id WHERE A.email=:email;");
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
		// 該当データなしの場合、NullPointerException発生
		try {
			User user = template.queryForObject(sql.toString(), param, USER_ROW_MAPPER);
			return user;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * ユーザ登録を行う.
	 * 
	 * @param user ユーザ情報
	 */
	public void insert(User user) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone)"
				+ " VALUES (:name, :email, :password, :zipcode, :address, :telephone)";
		template.update(sql, param);
	}

	/**
	 * 主キーでユーザ検索を行う.
	 * 
	 * @param id ユーザID
	 * @return ユーザ情報
	 */
	public User load(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.id, A.name, A.email, A.password, A.zipcode, A.address, A.telephone, B.image ");
		sql.append("FROM users AS A LEFT OUTER JOIN user_images AS B ON A.id=B.user_id WHERE A.id=:id;");
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		User user = template.queryForObject(sql.toString(), param, USER_ROW_MAPPER);
		return user;
	}

	/**
	 * パスワード以外のユーザー情報の更新を行う.
	 * 
	 * @param user ユーザー情報
	 */
	public void Update(User user) {
		StringBuilder sql = new StringBuilder();

		if (user.getImage() == null) {
			// 画像なしの場合、画像テーブルは更新しない
			sql.append("UPDATE users SET name=:name, email=:email, zipcode=:zipcode, ");
			sql.append("address=:address, telephone=:telephone WHERE id=:id;");
		} else {
			// 画像ありの場合、画像テーブルも更新
			sql.append("WITH returning_user_id AS( ");
			sql.append("UPDATE users SET name=:name, email=:email, zipcode=:zipcode, ");
			sql.append("address=:address, telephone=:telephone WHERE id=:id RETURNING id) ");
			sql.append("UPDATE user_images SET image=:image WHERE user_id IN (SELECT id FROM returning_user_id);");
		}

		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		template.update(sql.toString(), param);
	}

}
