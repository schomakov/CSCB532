package com.nbu.CSCB532.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model) {
        return "home";
    }

    @GetMapping("/error")
    public String errorPage(Model model) {
        return "error";
    }
}