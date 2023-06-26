package com.beautycenter.adbproject2.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            Model model
    ) {
        String sql = "SELECT \"final\".loginuser(?,?)";
        boolean isValidUser = jdbcTemplate.queryForObject(sql, Boolean.class, username, password);

        if (isValidUser) {
            String clientIdSql = "SELECT id FROM \"final\".USERS WHERE username = ?";
            int clientUserId = jdbcTemplate.queryForObject(clientIdSql, Integer.class, username);

            HttpSession session = request.getSession();
            session.setAttribute("clientId", clientUserId);

            return "redirect:/home"+clientUserId;
        } else {
            return "redirect:/login?error";
        }
    }

}
