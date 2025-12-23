package com.example.flea_market_app.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.flea_market_app.service.UserService;

@Controller
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 新規会員登録画面を表示する
	 */
	@GetMapping("/register")
	public String showRegisterForm() {
		// templates/register.html を呼び出す
		return "register";
	}

	/**
	 * 新規会員登録処理を実行し、成功したら自動ログインして商品一覧へ
	 */
	@PostMapping("/register")
	public String registerUser(@RequestParam String name,
			@RequestParam String email,
			@RequestParam String password,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

		// 1. メールアドレスの重複チェック
		if (userService.getUserByEmail(email).isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "このメールアドレスは既に登録されています");
			return "redirect:/register";
		}

		try {
			// 2. ユーザーをDBに保存 (UserService内で {noop} を付加し、Roleを "USER" に設定すること)
			userService.registerNewUser(name, email, password);

			// 3. 自動ログイン処理 (HttpServletRequest.login を使用)
			// loginメソッドには「生のパスワード」を渡します
			request.login(email, password);

			// 4. 成功したら商品一覧画面へ遷移
			return "redirect:/items";

		} catch (ServletException e) {
			// 自動ログイン（認証）に失敗した場合はログイン画面へ
			return "redirect:/login";
		} catch (Exception e) {
			// DB保存エラー（制約違反など）が発生した場合
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "登録に失敗しました。入力内容を確認してください");
			return "redirect:/register";
		}
	}
}