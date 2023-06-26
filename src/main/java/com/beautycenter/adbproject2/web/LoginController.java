package com.beautycenter.adbproject2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoginController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            return "login-error";
        } else {
            return "login";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        String sql = "SELECT \"final\".loginuser(?,?)";
        boolean isValidUser = jdbcTemplate.queryForObject(sql, Boolean.class, username, password);

        if (isValidUser) {
            // Redirect to the home page or any other authorized page
            return "redirect:/home";
        } else {
            // Redirect to a login error page or display an error message
            return "redirect:/login?error";
        }
    }

}
