package com.example9.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.web.multipart.MultipartFile;

/**
 * ユーザ情報変更への入力情報.
 * 
 * @author suzukikunpei
 *
 */
public class ChangeUserInfoForm {

	/** 名前 */
	@NotBlank(message = "お名前を入力して下さい")
	private String name;

	/** メールアドレス */
	@Email(message = "アドレスが不正です")
	@NotBlank(message = "メールアドレスを入力して下さい")
	private String email;

	/** 郵便番号 */
	@NotBlank(message = "郵便番号を入力して下さい")
	@Pattern(regexp = "^[0-9]{7}$", message = "郵便番号はハイフン無の7桁で入力して下さい")
	private String zipcode;

	/** 住所 */
	@NotBlank(message = "住所を入力して下さい")
	private String address;

	/** 電話番号 */
	@NotBlank(message = "電話番号を入力して下さい")
	@Pattern(regexp = "^[0-9]+$", message = "電話番号は数値を入力して下さい")
	private String telephone;

	/** アイコン画像 */
	private MultipartFile image;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "ChangeUserInfoForm [name=" + name + ", email=" + email + ", zipcode=" + zipcode + ", address=" + address
				+ ", telephone=" + telephone + ", image=" + image + "]";
	}

}
