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

import java.util.List;
import java.util.Map;

@Controller
public class AddCertificate {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddCertificate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/add_certificate")
    public String addCertificate() {
        return "add_certificate";
    }

    @PostMapping("/add_certificate")
    public String getCertificate(@RequestParam("certificateType") String certificateType,
                                 HttpServletRequest request) {
        HttpSession session = request.getSession();

        // Check if the logged-in user is an employee

        Integer loggedEmployeeId = (Integer) session.getAttribute("clientId");
        if (loggedEmployeeId == null) {
            return "redirect:/login"; // Redirect to login page if user is not logged in
        }

        String checkEmployeeSql = "SELECT COUNT(*) FROM \"final\".MANAGER WHERE employeeuserid = ?";

        int count = jdbcTemplate.queryForObject(checkEmployeeSql, Integer.class, loggedEmployeeId);
        if (count == 0) {
            return "redirect:/login"; // Redirect to access denied page if user is not an employee
        }

        jdbcTemplate.execute(String.format("SELECT \"final\".add_certificate('%s', '%s');",
                loggedEmployeeId, certificateType));


        return "redirect:/manager_certificate";
    }

}
