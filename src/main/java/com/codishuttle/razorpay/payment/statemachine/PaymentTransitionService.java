package com.codishuttle.razorpay.payment.statemachine;

import com.codishuttle.razorpay.common.enums.PaymentActor;
import com.codishuttle.razorpay.common.enums.PaymentEvent;
import com.codishuttle.razorpay.common.enums.PaymentStatus;
import com.codishuttle.razorpay.payment.entity.Payment;
import com.codishuttle.razorpay.payment.entity.PaymentTransitionLog;
import com.codishuttle.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);



        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM) //todo: fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();

        payment.setStatus(next);

        paymentTransitionLogRepository.save(log);

        return next;

    }

}
