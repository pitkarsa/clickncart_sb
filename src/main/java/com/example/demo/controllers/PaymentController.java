package com.example.demo.controllers;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Orders;
import com.example.demo.models.User;
import com.example.demo.services.OrderService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
//@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class PaymentController {

	@Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;
    
    @Autowired
    private OrderService orderService;
	
	@PostMapping("/api/create-order")
	public ResponseEntity<?> createRazorpayOrder(@RequestBody Map<String, Object> data) {
        try {
            int amount = (int) data.get("amount");

            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);  // amount in paise(subunits)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_12345");

            Order order = client.orders.create(orderRequest);
//            System.out.println("create order...."+order);
            return new ResponseEntity(Map.of("orderId", order.get("id"),"key", keyId), HttpStatus.OK);

        } catch (Exception e) {
        	return new ResponseEntity<>("Error:"+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
	
	 // Payment Signature Verification
    @PostMapping("/api/verify")
    public ResponseEntity<?> verifyPayment(
    		@AuthenticationPrincipal User user,
    		@RequestBody Map<String, String> data) {
        try {
//        	System.out.println("PRINCIPAL USER"+user);
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");
            System.out.println("Verify payment:::: "+orderId+" "+paymentId);
            String generatedSignature = Utils.getHash(orderId + "|" + paymentId, keySecret);

            if (generatedSignature.equals(signature)) {
            	Orders o = new Orders();
            	o.setOrderId(orderId);
            	o.setPaymentId(paymentId);
            	o.setUser(user);
            	orderService.placeOrder(o);
                return ResponseEntity.ok("Payment Verified");
            } 
            else {
                return ResponseEntity.status(400).body("Invalid Signature");
            }
        } 
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
	
}
