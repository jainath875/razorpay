package com.codishuttle.razorpay.payment.service.impl;

import com.codishuttle.razorpay.common.enums.OrderStatus;
import com.codishuttle.razorpay.common.enums.PaymentEvent;
import com.codishuttle.razorpay.common.enums.PaymentStatus;
import com.codishuttle.razorpay.common.exceptions.BusinessRulesViolationException;
import com.codishuttle.razorpay.common.exceptions.ResourceNotFoundException;
import com.codishuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codishuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codishuttle.razorpay.payment.entity.OrderRecord;
import com.codishuttle.razorpay.payment.entity.Payment;
import com.codishuttle.razorpay.payment.gateway.PaymentGatewayRouter;
import com.codishuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codishuttle.razorpay.payment.gateway.dto.PaymentResult;
import com.codishuttle.razorpay.payment.mapper.PaymentMapper;
import com.codishuttle.razorpay.payment.repository.OrderRepository;
import com.codishuttle.razorpay.payment.repository.PaymentRepository;
import com.codishuttle.razorpay.payment.service.PaymentService;
import com.codishuttle.razorpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final PaymentGatewayRouter paymentGatewayRouter;

    private final PaymentMapper paymentMapper;

    private final PaymentTransitionService paymentTransitionService;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {

        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));

        if(order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRulesViolationException("ORDER_NOT_PAYABLE", "order cannot accept payment in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .build();

        paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(
                payment.getId(),
                request.orderId(),
                merchantId,
                order.getAmount(),
                request.method(),
                request.methodDetails()
        );

       PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
//                payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {
            }
        }

        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        //todo: send an outbox (kafka event)


        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {

        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));


        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

        if(paymentResult instanceof PaymentResult.Success success) {
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment captured, paymentId: {}", paymentId);

        } else if(paymentResult instanceof PaymentResult.Failure failure) {
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());

            log.warn("Payment captured failed, paymentId: {}", paymentId);

        }

        payment = paymentRepository.save(payment);

        //todo: send an outbox (kafka event)

        return paymentMapper.toResponse(payment);
    }
}





























