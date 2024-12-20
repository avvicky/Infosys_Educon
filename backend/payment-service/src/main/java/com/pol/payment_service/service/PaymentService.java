package com.pol.payment_service.service;

import com.pol.payment_service.client.ProductFeignClient;
import com.pol.payment_service.dto.CoursePriceDTO;
import com.pol.payment_service.dto.PaymentRequestDTO;
import com.pol.payment_service.dto.PaymentResponse;
import com.pol.payment_service.entity.Payment;
import com.pol.payment_service.mapper.PaymentMapper;
import com.pol.payment_service.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final RazorpayClient razorpay;
    private final PaymentRepository paymentRepository;
    private final ProductFeignClient productFeignClient;

    public PaymentService(RazorpayClient razorpay, PaymentRepository paymentRepository, ProductFeignClient productFeignClient) {
        this.razorpay = razorpay;
        this.paymentRepository = paymentRepository;
        this.productFeignClient = productFeignClient;
    }

    @Value("${razorpay.key_secret}")
    private String keySecret;


    public com.pol.payment_service.entity.Payment createOrder(PaymentRequestDTO paymentRequestDTO) throws RazorpayException {
        if(paymentRequestDTO.getProductId()==null){
            throw new RuntimeException("Please provide product id");
        }
        UUID productId = paymentRequestDTO.getProductId();
        CoursePriceDTO coursePriceDTO = productFeignClient.getCoursePriceById(productId);
        if(coursePriceDTO==null){
            throw new RuntimeException("Product not found with id : "+productId+" in payment service.");
        }
        JSONObject options = new JSONObject();
        options.put("amount", coursePriceDTO.getPrice().multiply(new BigDecimal(100)));
        options.put("currency", "INR");
        options.put("receipt", UUID.randomUUID().toString());

        Order order = razorpay.orders.create(options);
        Payment payment = PaymentMapper.toEntity(order);
        payment.setProductId(paymentRequestDTO.getProductId());
        payment.setUserId(UUID.randomUUID());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        return payment;
    }

    public String updateOrder(Map<String,String> paymentDetails) throws RazorpayException {
        String razorpay_payment_id = paymentDetails.get("razorpay_payment_id");
        String razorpay_order_id = paymentDetails.get("razorpay_order_id");
        String razorpay_signature = paymentDetails.get("razorpay_signature");

        if(!paymentRepository.existsById(UUID.fromString(razorpay_order_id))){
            return "ORDER DOESN'T EXIST";
        }
        JSONObject options = new JSONObject(paymentDetails);
        boolean verified = Utils.verifyPaymentSignature(options,keySecret);
        if(verified){
            return "Successful";
        }
        return "Failed payment";
    }
}
