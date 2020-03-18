package com.example9.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example9.domain.User;
import com.example9.form.ChangeUserInfoForm;
import com.example9.service.UserRegisterService;

/**
 * ユーザー情報の変更を行うコントローラ.
 * 
 * @author mayumiono
 *
 */
@Controller
@RequestMapping("/change-user")
public class ChangeUserInfoController {

	@Autowired
	private UserRegisterService userRegisterService;

	@Autowired
	private HttpSession session;

	@ModelAttribute
	public ChangeUserInfoForm setUpForm() {
		User user = userRegisterService.showUser((Integer) session.getAttribute("userId"));
		ChangeUserInfoForm changeUserInfoForm = new ChangeUserInfoForm();
		BeanUtils.copyProperties(user, changeUserInfoForm);
		return changeUserInfoForm;
	}

	/**
	 * ユーザー情報変更ページの表示.
	 * 
	 * @return ユーザー情報変更画面
	 */
	@RequestMapping("")
	public String showFormPage() {
		return "change_user_info";
	}

	/**
	 * ユーザー情報の更新.
	 * 
	 * @param form   ユーザー情報（フォーム入力値）
	 * @param result 入力チェック
	 * @return 商品一覧（不正入力時はユーザー情報変更画面）
	 */
	@RequestMapping("/input")
	public String inputInfo(@Validated ChangeUserInfoForm form, BindingResult result) throws IOException {

		// 画像ファイル形式チェック
		MultipartFile imageFile = form.getImage();
		String fileExtension = null;
		boolean imageExists = true;
		try {
			fileExtension = getExtension(imageFile.getOriginalFilename());

			if (!"jpg".equals(fileExtension) && !"png".equals(fileExtension) && !"gif".equals(fileExtension)) {
				result.rejectValue("image", "", "拡張子は.jpg/.png/.gifのみに対応しています");
			}
		} catch (NullPointerException e) {
			// 画像ファイルがアップロードされなかった場合
			imageExists = false;
		} catch (FileNotFoundException e) {
			// ファイル名に拡張子が見当たらない場合
			result.rejectValue("image", "", "拡張子は.jpg/.png/.gifのみに対応しています");
		}

		if (result.hasErrors()) {
			return "change_user_info";
		}

		// フォームへの入力情報をドメインへコピー
		User user = new User();
		BeanUtils.copyProperties(form, user);
		user.setId((Integer) session.getAttribute("userId"));

		// 画像ファイルをBase64形式にエンコードしてドメインへセット
		if (imageExists) {
			String base64FileString = Base64.getEncoder().encodeToString(imageFile.getBytes());
			System.out.println(base64FileString);
			if ("jpg".equals(fileExtension)) {
				base64FileString = "data:image/jpeg;base64," + base64FileString;
			} else if ("png".equals(fileExtension)) {
				base64FileString = "data:image/png;base64," + base64FileString;
			} else if ("gif".equals(fileExtension)) {
				base64FileString = "data:image/gif;base64," + base64FileString;
			}
			user.setImage(base64FileString);
		}

		userRegisterService.changeUserInfo(user);

		return "forward:/";
	}

	/*
	 * <<<<<<< HEAD ファイル名から拡張子を返す. ======= ファイル名から拡張子を返します. >>>>>>>
	 * 2f205365d037c254f6d538d6eb5caf4c24c54344
	 * 
	 * @param originalFileName ファイル名
	 * 
	 * @return .を除いたファイルの拡張子
	 */
	private String getExtension(String originalFileName) throws NullPointerException, FileNotFoundException {
		if (originalFileName == null || "".equals(originalFileName)) {
			throw new NullPointerException();
		}
		int point = originalFileName.lastIndexOf(".");
		if (point == -1) {
			// ファイル名に拡張子がついていない場合は例外とする
			throw new FileNotFoundException();
		}
		return originalFileName.substring(point + 1);
	}
}