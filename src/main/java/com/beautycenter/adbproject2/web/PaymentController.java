//package com.beautycenter.adbproject2.web;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.relational.core.sql.In;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.StatementCallback;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import java.sql.ResultSet;
//
//
//@Controller
//public class PaymentController {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public PaymentController(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @GetMapping("/payment")
//    public String getPayment() {
//        return "payment";
//    }
//
//    @PostMapping("/payment")
//    public String makePayment(@RequestParam(name = "typeofpayment") String typeofpayment,
//                           @RequestParam(name = "card_number", required = false) int card_number,
//                           @RequestParam(name = "cardholder_name", required = false) String cardholder_name,
//                           @RequestParam(name = "cvv", required = false) int cvv,
//                           @RequestParam(name = "expiry_date", required = false) String expiry_date,
//                           @RequestParam(name = "payment_info", required = false) String payment_info) {
////        String sql = "SELECT \"final\".make_payment(?, ?, ?, ?, ?, ?)";
////        Object[] params = {typeofpayment, card_number, cardholder_name, cvv, expiry_date, payment_info};
////
////        int paymentId = jdbcTemplate.execute(sql,params,,Integer.class);
////        jdbcTemplate.execute(String.format("SELECT \"final\".make_payment('%s', '%s', '%s', '%s', '%s', '%s');",
////                typeofpayment, card_number, cardholder_name, cvv, expiry_date, payment_info));
//
//        String sql = String.format("SELECT \"final\".make_payment('%s', '%s', '%s', '%s', '%s', '%s');",
//                typeofpayment, card_number, cardholder_name, cvv, expiry_date, payment_info);
//        Integer paymentId = jdbcTemplate.execute((StatementCallback<Integer>) statement -> {
//            statement.execute(sql);
//            ResultSet resultSet = statement.getResultSet();
//            if (resultSet.next()) {
//                return resultSet.getInt(1);
//            }
//            return null;
//        });
//
//        return "payment/?paymentId="+paymentId;
//
//    }
//
//
//}
//
