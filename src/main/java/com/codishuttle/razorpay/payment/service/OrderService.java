package com.codishuttle.razorpay.payment.service;

import com.codishuttle.razorpay.payment.dto.request.OrderRequest;
import com.codishuttle.razorpay.payment.dto.response.OrderResponse;
import com.codishuttle.razorpay.payment.dto.response.PaymentResponse;
import org.hibernate.query.Order;


import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, OrderRequest request);

    OrderResponse getById(UUID merchantId, UUID orderId);

    OrderResponse cancel(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}
