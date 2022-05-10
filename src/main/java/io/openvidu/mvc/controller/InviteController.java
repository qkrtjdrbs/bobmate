package io.openvidu.mvc.controller;

import io.openvidu.dto.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import static io.openvidu.Const.LOGIN_USER;

@Controller
@Slf4j
public class InviteController {

    @GetMapping("/invite")
    public String invited(
            @RequestParam("session-name") String sessionName,
            @SessionAttribute(required = false, name = LOGIN_USER) SessionUser loginUser,
            Model model
    ) {
        model.addAttribute("user", loginUser);
        if(loginUser == null){
            log.info("login user doesn't exist : default home");
            return "index";
        } else {
            log.info("login user exist : dashboard");
            model.addAttribute("username", loginUser.getUsername());
            model.addAttribute("sessionName", sessionName);
            return "invited-dashboard";
        }
    }

}
