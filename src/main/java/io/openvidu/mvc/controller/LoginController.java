package io.openvidu.mvc.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import io.openvidu.dto.LoginDto;
import io.openvidu.dto.SessionUser;
import io.openvidu.entity.User;
import io.openvidu.mvc.service.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.openvidu.java.client.OpenViduRole;

import static io.openvidu.Const.LOGIN_USER;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

	private final UserService userService;

	public class MyUser {

		String name;
		String pass;
		OpenViduRole role;

		public MyUser(String name, String pass, OpenViduRole role) {
			this.name = name;
			this.pass = pass;
			this.role = role;
		}
	}

	public static Map<String, MyUser> users = new ConcurrentHashMap<>();

	@GetMapping("/user/login")
	public String login(@ModelAttribute("user") LoginDto user,
						@SessionAttribute(name = LOGIN_USER, required = false) SessionUser loginUser
	){
		log.info("login controller");
		if(loginUser == null){
			log.info("not login");
			return "login";
		}
		return "redirect:/";
	}

	@PostMapping("/user/login")
	public String login(
			@Valid @ModelAttribute("user") LoginDto user,
			BindingResult bindingResult,
			HttpServletRequest request,
			@RequestParam(defaultValue = "/") String redirectURL
	){
		if(bindingResult.hasErrors()){
			return "login";
		}

		User loginUser = userService.login(user);
		if(loginUser == null){
			bindingResult.reject("loginFail");
			return "login";
		}

		HttpSession session = request.getSession();
		session.setAttribute(LOGIN_USER, new SessionUser(loginUser));

		return "redirect:" + redirectURL;
	}

	@GetMapping("/user/logout")
	public String logout(HttpServletRequest request) {
		log.info("로그아웃 실행");
		HttpSession session = request.getSession(false);
		if(session != null){
			session.invalidate();
		}
		return "redirect:/";
	}

}