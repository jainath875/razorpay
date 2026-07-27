package com.codishuttle.razorpay.payment.service.impl;

import com.codishuttle.razorpay.common.enums.OrderStatus;
import com.codishuttle.razorpay.common.exceptions.BusinessRulesViolationException;
import com.codishuttle.razorpay.common.exceptions.DuplicateResourceException;
import com.codishuttle.razorpay.common.exceptions.ResourceNotFoundException;
import com.codishuttle.razorpay.payment.dto.request.OrderRequest;
import com.codishuttle.razorpay.payment.dto.response.OrderResponse;
import com.codishuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codishuttle.razorpay.payment.entity.OrderRecord;
import com.codishuttle.razorpay.payment.entity.Payment;
import com.codishuttle.razorpay.payment.mapper.OrderMapper;
import com.codishuttle.razorpay.payment.mapper.PaymentMapper;
import com.codishuttle.razorpay.payment.repository.OrderRepository;
import com.codishuttle.razorpay.payment.repository.PaymentRepository;
import com.codishuttle.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, OrderRequest request) {

        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order with receipt already exists: " + request.receipt());
        }

        OrderRecord order =  OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .merchantId(merchantId)
                .notes(request.notes())
                .status(OrderStatus.CREATED)
                .expiresAt(request.expiresAt() != null ? request.expiresAt() : LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes)) // default expiry time is 30 minutes
                .build();

        orderRepository.save(order);

        //Todo: publish kafka event about order creation

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord order =  orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {

        OrderRecord order =  orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if(order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.PAID) {
            throw new BusinessRulesViolationException("ORDER_CANNOT_CANCEL", "Cannot cancel order with status: " + order.getStatus().name());
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {

        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<Payment> paymentList = paymentRepository.findByOrder_Id(order);

//        return paymentList.stream().map(
//                paymentMapper::toResponse
//        ).toList();

        return paymentMapper.toResponseList(paymentList);
    }
}
