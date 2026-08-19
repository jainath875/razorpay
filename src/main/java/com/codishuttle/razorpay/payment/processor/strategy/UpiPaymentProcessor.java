package com.codishuttle.razorpay.payment.processor.strategy;

import com.codishuttle.razorpay.common.util.RandomizerUtil;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codishuttle.razorpay.payment.processor.PaymentProcessor;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import org.springframework.stereotype.Component;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "fail@okaxis";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("vpa").toString() : null;

        //simulation
        if (VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED", "Banked rejected the transaction registration");
        }

        String processorRef = "UPI_PROCESSOR" + RandomizerUtil.randomBase64(16);


        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
