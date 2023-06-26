package com.beautycenter.adbproject2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class ViewsController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ViewsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    @GetMapping("/manager_certificate")
    public String getManagerCertificateCount(Model model) {
        String sql = "SELECT * FROM \"final\".manager_certificate_count";
        List<Map<String, Object>> managerCertificates = jdbcTemplate.queryForList(sql);
        model.addAttribute("managerCertificates", managerCertificates);
        return "manager_certificate";
    }

    @GetMapping("/employee_certificate")
    public String getEmployeeCertificateManager(Model model) {
        String sql = "SELECT * FROM \"final\".employee_certificate_manager";
        List<Map<String, Object>> employeeCertificates = jdbcTemplate.queryForList(sql);
        model.addAttribute("employeeCertificates", employeeCertificates);
        return "employee_certificates";
    }

}
