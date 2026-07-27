package com.codishuttle.razorpay.merchant.service;

import com.codishuttle.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.codishuttle.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
}
