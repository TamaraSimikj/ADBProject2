package com.beautycenter.adbproject2.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AppointmentController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/beautycenters")
    public String getAllBeautyCentres(Model model) {
        String sql = "SELECT * FROM \"final\".beautycenter";
        List<Map<String,Object>> bc =jdbcTemplate.queryForList(sql);
        model.addAttribute("beautyCenters", bc);
        return "beautycenters";
    }

    @GetMapping("/services/{id}")
    public String getServicesByBeautyCenter(@PathVariable int id, Model model) {
        String sql = "SELECT id, service_category, \"value\" FROM \"final\".services WHERE bcID =" +id;
        List<Map<String,Object>> services =jdbcTemplate.queryForList(sql);
        model.addAttribute("services", services);
        model.addAttribute("id", id);
        return "services";
    }

    @PostMapping("/services/{id}")
    public String submitSelectedServices(
            @PathVariable int id,
            @RequestParam(name = "selectedServices") List<Integer> selectedServices)
    {
        if (selectedServices != null && !selectedServices.isEmpty()) {
            // Convert the list of selected service IDs to a query parameter string
            String serviceIds = selectedServices.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            String encodedServiceIds = URLEncoder.encode(serviceIds, StandardCharsets.UTF_8);
            return "redirect:/employeelistfromservices?bcId=" + id + "&services=" + encodedServiceIds;
        } else {
            // No services selected, handle the case accordingly
            return "redirect:/services?error";
        }
    }

    @GetMapping("/employeelistfromservices")
    public String getEmployeesForServices(@RequestParam(name = "services") String serviceIds,
                                          @RequestParam(name = "bcId") int bcId,
                                          Model model) {

        String sql = "SELECT * FROM \"final\".employeesForServices WHERE bcId = ? AND id IN (" + serviceIds + ")";
        List<Map<String, Object>> employees = jdbcTemplate.queryForList(sql, bcId);

        model.addAttribute("employees", employees);
        model.addAttribute("services", serviceIds);

        return "employeelistfromservices";
    }

    @GetMapping("/booking-times")
    public String getBookingTimes(
            @RequestParam("bcId") int beautyCenterId,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("services") String serviceIds,
            Model model
    ) {
        String encodedServiceIds = URLEncoder.encode(serviceIds, StandardCharsets.UTF_8);

        // Fetch the booking times based on the provided parameters
        String sql = "SELECT * FROM \"final\".free_appointments WHERE employeeuserid = ?" +
                " AND BeautyCenterID =" + beautyCenterId +
            " AND ServiceID IN (" + serviceIds +")";
        List<Map<String, Object>> bookingTimes = jdbcTemplate.queryForList(sql, employeeId);

        // Pass the booking times to the Thymeleaf template
        model.addAttribute("bookingTimes", bookingTimes);

        return "booking-times";
    }

    @PostMapping("/booking-time")
    public String submitBookingTime(@RequestParam("bookingTimeId") int bookingTimeId) {
        // Process the selected booking time
        System.out.println("Selected booking time ID: " + bookingTimeId);

        // Redirect or handle the processing result accordingly
        return "redirect:/booking-confirmation?bookingTimeId=" + bookingTimeId;
    }





}
