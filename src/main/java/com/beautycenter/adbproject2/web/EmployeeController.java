package com.beautycenter.adbproject2.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class EmployeeController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    //Employee opens booking time so proverka dali logiraniot e employee ili ne
    @PostMapping("/addBookingTime")
    public String addBookingTime(@RequestParam("startTime") String startTime,
                                 @RequestParam("duration") int duration,
                                 @RequestParam("employeeUserId") int employeeUserId,
                                 HttpServletRequest request) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, formatter);

            // Check if the logged-in user is an employee
            HttpSession session = request.getSession();
            Integer loggedEmployeeId = (Integer) session.getAttribute("employeeId");
            if (loggedEmployeeId == null) {
                return "redirect:/login"; // Redirect to login page if user is not logged in
            }

            String checkEmployeeSql = "SELECT COUNT(*) FROM \"final\".EMPLOYEES WHERE id = ?";

            int count = jdbcTemplate.queryForObject(checkEmployeeSql, Integer.class, loggedEmployeeId);
            if (count == 0) {
                return "redirect:/access-denied"; // Redirect to access denied page if user is not an employee
            }

            String addBookingTimeSql = "SELECT \"final\".addbookingtime(?, ?, ?)";

            int bookingTimeId = jdbcTemplate.queryForObject(addBookingTimeSql, Integer.class, parsedStartTime, duration, employeeUserId);

            return "redirect:/booking-times/" + bookingTimeId;
        } catch (Exception e) {
            return "redirect:/booking-times?error";
        }
    }


    //update booking time for appointment
    @PostMapping("/appointments/{id}/updateBookingTime")
    public String updateAppointmentBookingTime(@PathVariable int id,
                                               @RequestParam("appointmentId") int appointmentId,
                                               @RequestParam("bookingTimeId") int bookingTimeId) {
        try {
            String updateBookingTimeSql = "CALL \"final\".update_appointment_booking_time(?, ?)";
            jdbcTemplate.update(updateBookingTimeSql, appointmentId, bookingTimeId);

            return "redirect:/appointments/" + id;
        } catch (Exception e) {
            return "redirect:/appointments/" + id + "?error";
        }
    }

    //cancel appointment
    @PostMapping("/appointments/{id}/cancelAppointment")
    public String cancelAppointment(@PathVariable int id,
                                    @RequestParam("appointmentId") int appointmentId) {
        try {
            String cancelAppointmentSql = "CALL \"final\".cancel_appointment(?)";
            jdbcTemplate.update(cancelAppointmentSql, appointmentId);

            return "redirect:/appointments/" + id;
        } catch (Exception e) {
            return "redirect:/appointments/" + id + "?error";
        }
    }



}
