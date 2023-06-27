package com.beautycenter.adbproject2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HomeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = {"/", "/home"})
    public String defaultMapping() {
        return "home";
    }

//    @GetMapping("/appointments/{id}")
//    public String getClientAppointments(@PathVariable int id, Model model) {
//
//        // String sql = "SELECT * FROM \"final\".APPOINTMENTS_LIST WHERE ClientID = ?";
//
//        String sql = "SELECT a.id, a.bookingtimeid, a_s.serviceid, a.clientuserid " +
//                "bt.start_time, s.service_category, r.rev_comment, r.rating " +
//                "FROM \"final\".appointment a " +
//                "JOIN \"final\".booking_time bt ON a.bookingtimeid = bt.id " +
//                "JOIN \"final\".appointment_service a_s ON a.id = a_s.appointmentid " +
//                "JOIN \"final\".service s ON a_s.serviceid = s.id " +
//                "LEFT JOIN \"final\".review r ON a.id = r.appointmentid " +
//                "WHERE a.clientuserid = ? AND bt.start_time>now() " +
//                "ORDER BY bt.start_time DESC";
//
//        List<Map<String, Object>> appointments = jdbcTemplate.queryForList(sql, id);
//
//
//        model.addAttribute("appointments", appointments);
//
//        return "clientAppointments";
//    }
}
