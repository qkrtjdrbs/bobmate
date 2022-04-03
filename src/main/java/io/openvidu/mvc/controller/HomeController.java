package io.openvidu.mvc.controller;

import io.openvidu.Const;
import io.openvidu.dto.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;


import static io.openvidu.Const.*;

@Controller
@Slf4j
public class HomeController {

    @GetMapping(value = {"/", "/dashboard"})
    public String home(
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
            return "dashboard";
        }
    }
}
