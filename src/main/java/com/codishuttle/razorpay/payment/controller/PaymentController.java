package com.codishuttle.razorpay.payment.controller;

import com.codishuttle.razorpay.merchant.security.MerchantContext;
import com.codishuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codishuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codishuttle.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@RequestBody @Valid PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                paymentService.initiate(merchantContext.getMerchantId(), request)
        );
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId){
        return ResponseEntity.ok(paymentService.capture(merchantContext.getMerchantId(), paymentId));
    }

}
