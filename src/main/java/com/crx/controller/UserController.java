package com.crx.controller;

import com.crx.entity.User;
import com.crx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(User user, HttpSession session){
        User user1 = userService.login(user);
        if(user1 != null){
            session.setAttribute("username",user1.getUsername());
            session.setAttribute("userId",user1.getId());
            return "redirect:/file/showAll";
        }else{
            return "redirect:/login.html";
        }
    }
}
