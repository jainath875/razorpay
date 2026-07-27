package com.codishuttle.razorpay.payment.dto.request;

import com.codishuttle.razorpay.common.entity.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record OrderRequest(
        @NotNull(message = "Amount is required")
        Money amount,

        @Size(max = 100)
        String receipt, //order-id(known to merchant)

        Map<String, Object> notes,

        LocalDateTime expiresAt
) {
}
