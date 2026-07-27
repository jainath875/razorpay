package com.codishuttle.razorpay.payment.service;

import com.codishuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codishuttle.razorpay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);
}
