package com.codishuttle.razorpay.vault.service;

import com.codishuttle.razorpay.common.entity.Money;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codishuttle.razorpay.vault.dto.request.TokenizeRequest;
import com.codishuttle.razorpay.vault.dto.response.TokenizeResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
