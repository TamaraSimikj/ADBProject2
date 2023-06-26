package com.beautycenter.adbproject2.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "services";
    }

    @PostMapping("/submit-services")
    public String submitSelectedServices(@RequestParam(name = "selectedServices", required = false) List<Integer> selectedServices,
                                         @RequestParam(name = "beautyCenterId") int beautyCenterId) {
        // Process the selected services
        if (selectedServices != null && !selectedServices.isEmpty()) {
            // Convert the list of selected service IDs to a query parameter string
            String serviceIds = selectedServices.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // Redirect to the employeelistfromservices view with the selected services and beauty center ID as query parameters
            return "redirect:/employeelistfromservices?services=" + serviceIds + "&beautyCenterId=" + beautyCenterId;
        } else {
            // No services selected, handle the case accordingly
            return "redirect:/services?error";
        }
    }

    @GetMapping("/employeelistfromservices")
    public String getEmployeesForServices(@RequestParam(name = "services") String serviceIds,
                                          @RequestParam(name = "beautyCenterId") int beautyCenterId,
                                          Model model) {
        // Retrieve employees for the selected services and beauty center ID from the database
        String sql = "SELECT * FROM employeesForServices WHERE bcId = ? AND serviceId IN (" + serviceIds + ")";
        List<Map<String, Object>> employees = jdbcTemplate.queryForList(sql, beautyCenterId);

        // Add the employees to the model
        model.addAttribute("employees", employees);

        return "employeelistfromservices";
    }
}
