package com.beautycenter.adbproject2.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final JdbcTemplate jdbcTemplate;

  @Autowired
    public RegistrationController (JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate=jdbcTemplate;
    }

    @GetMapping("/register")
    public String getRegistration()
    {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String cName,
            @RequestParam String surname,
            @RequestParam String phoneNumber,
            @RequestParam String email
    )
    {
        jdbcTemplate.execute(String.format("CALL \"final\".registerNewClient('%s', '%s', '%s', '%s', '%s', '%s');",
                username,password,cName,surname,phoneNumber,email));
        return "redirect:/home";
    }
}