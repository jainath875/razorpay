package com.codishuttle.razorpay.merchant.dto.request;

import com.codishuttle.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
