package com.codishuttle.razorpay.merchant.service;


import com.codishuttle.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.codishuttle.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.codishuttle.razorpay.merchant.dto.response.ApiKeyResponse;
import com.fasterxml.classmate.AnnotationOverrides;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {


    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId);
}
