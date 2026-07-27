package com.codishuttle.razorpay.payment.processor;

import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);

}
