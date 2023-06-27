package com.beautycenter.adbproject2.web;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private HttpServletRequest request;

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
            return "redirect:/employeelistfromservices?bcId=" + id + "&serviceIds=" + encodedServiceIds;
        } else {
            // No services selected, handle the case accordingly
            return "redirect:/serviceIds?error";
        }
    }

    @GetMapping("/employeelistfromservices")
    public String getEmployeesForServices(@RequestParam(name = "serviceIds") String serviceIds,
                                          @RequestParam(name = "bcId") int bcId,
                                          Model model) {

        String sql = "SELECT * FROM \"final\".employeesForServices WHERE bcId = ? AND id IN (" + serviceIds + ")";
        List<Map<String, Object>> employees = jdbcTemplate.queryForList(sql, bcId);

        model.addAttribute("employees", employees);
        model.addAttribute("serviceIds", serviceIds);

        return "employeelistfromservices";
    }

    @GetMapping("/booking-times")
    public String getBookingTimes(
            @RequestParam("bcId") int beautyCenterId,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("serviceIds") String serviceIds,
            Model model
    ) {
//        String encodedServiceIds = URLEncoder.encode(serviceIds, StandardCharsets.UTF_8);

        // Fetch the booking times based on the provided parameters
        String sql = "SELECT * FROM \"final\".free_appointments WHERE employeeuserid = ?" +
                " AND BeautyCenterID =" + beautyCenterId +
            " AND ServiceID IN (" + serviceIds +")";
        List<Map<String, Object>> bookingTimes = jdbcTemplate.queryForList(sql, employeeId);

        // Pass the booking times to the Thymeleaf template
        model.addAttribute("bookingTimes", bookingTimes);
        model.addAttribute("employeeId",employeeId);
        model.addAttribute("bcId",beautyCenterId);
        model.addAttribute("serviceIds",serviceIds);

        return "booking-times";
    }

    @PostMapping("/booking-time")
    public String submitBookingTime(
            @RequestParam("bcId") int beautyCenterId,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("serviceIds") String serviceIds,
            @RequestParam("bookingTimeId") int bookingTimeId
    ) {
        // Process the selected booking time
        System.out.println("Selected booking time ID: " + bookingTimeId);

        // Redirect to the payment page with request parameters
        return "redirect:/payment?bookingTimeId=" + bookingTimeId +
                "&bcId=" + beautyCenterId +
                "&employeeId=" + employeeId +
                "&serviceIds=" + serviceIds;
    }


    @GetMapping("/payment")
    public String getPayment(
            @RequestParam("bookingTimeId") int bookingTimeId,
            @RequestParam("bcId") int beautyCenterId,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("serviceIds") String serviceIds,
            Model model
    ) {

        model.addAttribute("bookingTimeId", bookingTimeId);
        model.addAttribute("bcId", beautyCenterId);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("serviceIds", serviceIds);

        return "payment";
    }

    @PostMapping("/payment")
    public String processPayment(
            @RequestParam("bcId") int bcId,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("serviceIds") String serviceIds,
            @RequestParam("bookingTimeId") int bookingTimeId,
            @RequestParam("typeofpayment") String typeofPayment,
            @RequestParam(name = "cardholderName") String cardholderName,
            @RequestParam(name = "cardNumber") String cardNumber,
            @RequestParam(name = "cvv", required = false) String cvv,
            @RequestParam(name = "expiryDate", required = false) String expiryDate,
            @RequestParam(name = "paymentInfo", required = false) String paymentInfo,
            Model model
    ) {

        HttpSession session = request.getSession();
        int clientUserId = (int) session.getAttribute("clientId");
        List<Integer> serviceIdList = Arrays.stream(serviceIds.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        model.addAttribute("clientId",clientUserId);
        model.addAttribute("bcId",bcId);
        model.addAttribute("employeeId",employeeId);
        model.addAttribute("bookingTimeId",bookingTimeId);
        model.addAttribute("serviceIds",serviceIds);
        model.addAttribute("typeofPayment", typeofPayment);
        model.addAttribute("cardholderName", cardholderName);
        model.addAttribute("cardNumber", cardNumber);
        model.addAttribute("cvv", cvv);
        model.addAttribute("expiryDate", expiryDate);
        model.addAttribute("paymentInfo", paymentInfo);

        return "appointment-confirmation";
    }



//    @PostMapping("/appointment-confirmation")
//    public String bookingConfirmation(
//            @RequestParam(name = "clientId") int clientUserId,
//            @RequestParam("bookingTimeId") int bookingTimeId,
//            @RequestParam("employeeId") int employeeId,
//            @RequestParam("serviceIds") String serviceIds,
//            @RequestParam("typeofpayment") String typeofpayment,
//            @RequestParam(name = "cardholderName", required = false) String cardholderName,
//            @RequestParam(name = "cardNumber", required = false) String cardNumber,
//            @RequestParam(name = "cvv", required = false) String cvv,
//            @RequestParam(name = "expiryDate", required = false) String expiryDate,
//            @RequestParam(name = "paymentInfo", required = false) String paymentInfo
//    ) {
//
//        List<Integer> serviceIdList = Arrays.stream(serviceIds.split(","))
//                .map(Integer::valueOf)
//                .collect(Collectors.toList());
//
//        try {
//            if (typeofpayment.isEmpty()) {
//                typeofpayment = "Online";
//            }
//
//            String arrayLiteral = "{" + String.join(",", serviceIdList.stream().map(Object::toString).toArray(String[]::new)) +"}";
//            jdbcTemplate.execute(String.format("CALL \"final\".make_appointment('%s', '%s', '%s', ARRAY%s, '%s', '%s', '%s', '%s', '%s', '%s');",
//                    clientUserId, bookingTimeId, employeeId, serviceIdList, typeofpayment, cardNumber, cardholderName, cvv, expiryDate, paymentInfo));
//            return "redirect:/home";
//        } catch (Exception e) {
//            return "redirect:/home?error";
//        }
//    }

    @PostMapping("/appointment-confirmation")
    public String bookingConfirmation(
            @RequestParam(name = "clientId") int clientUserId,
            @RequestParam("bookingTimeId") int bookingTimeId,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("serviceIds") String serviceIds,
            @RequestParam("typeofpayment") String typeofpayment,
            @RequestParam(name = "cardholderName", required = false) String cardholderName,
            @RequestParam(name = "cardNumber", required = false) String cardNumber,
            @RequestParam(name = "cvv", required = false) String cvv,
            @RequestParam(name = "expiryDate", required = false) String expiryDate,
            @RequestParam(name = "paymentInfo", required = false) String paymentInfo
    ) {

        Integer[] serviceIdArray = Arrays.stream(serviceIds.split(","))
                .map(Integer::valueOf)
                .toArray(Integer[]::new);

        try {
            if (typeofpayment.isEmpty() && cardNumber.isEmpty()) {
                typeofpayment = "InStore";
            }else if(typeofpayment.isEmpty() && !cardNumber.isEmpty()){
                typeofpayment= "Online";
            }

            if (typeofpayment.equals("Online")) {
                jdbcTemplate.execute(String.format("CALL \"final\".make_appointment('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                        clientUserId, bookingTimeId, employeeId, serviceIdArray, typeofpayment, cardNumber, cardholderName, cvv, expiryDate, paymentInfo));
            } else if (typeofpayment.equals("InStore")) {
                jdbcTemplate.execute(String.format("CALL \"final\".make_appointment('%s', '%s', '%s', '%s', '%s', NULL, NULL, NULL, NULL, NULL);",
                        clientUserId, bookingTimeId, employeeId, serviceIdArray, typeofpayment));
            } else {
                // Handle invalid payment type
                return "redirect:/home?error";
            }

            return "redirect:/home";
        } catch (Exception e) {
            return "redirect:/home?error";
        }
    }





//    //Review
//    @GetMapping("/appointments/{id}")
//    public String getClientAppointments(@PathVariable int id, Model model) {
//
//        String sql = "SELECT a.id AS appointment_id, a.bookingtimeid, a_s.serviceid, " +
//                "bt.start_time, s.service_category, r.rev_comment, r.rating " +
//                "FROM appointment a " +
//                "JOIN booking_time bt ON a.bookingtimeid = bt.id " +
//                "JOIN appointment_service a_s ON a.id = a_s.appointmentid " +
//                "JOIN service s ON a_s.serviceid = s.id " +
//                "LEFT JOIN review r ON a.id = r.appointmentid " +
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

//    @PostMapping("/appointments/{id}/leave-review")
//    public String leaveReviewForAppointment(@PathVariable int id,
//                                            @RequestParam("appointmentId") int appointmentId,
//                                            @RequestParam("comment") String rev_comment,
//                                            @RequestParam("rating") int rating) {
//        try {
//            // Call the leave_review function to insert the review into the database
//            String leaveReviewSql = "SELECT leave_review(?, ?, ?, ?)";
//            jdbcTemplate.update(leaveReviewSql, id, appointmentId, rev_comment, rating);
//
//            return "redirect:/appointments/" + id;
//        } catch (Exception e) {
//            return "redirect:/appointments/" + id + "?error";
//        }
//


}
