package com.codishuttle.razorpay.payment.processor;

import com.codishuttle.razorpay.common.enums.PaymentMethod;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentProcessorRouter {

    private Map<PaymentMethod, PaymentProcessor> paymentProcessors;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request){
        PaymentProcessor processor = paymentProcessors.get(request.method());

        if (processor == null){
            throw new IllegalArgumentException("No Payment processor registered for payment method: " + request.method());
        }

        return processor.charge(request);

    }

}
