package com.codishuttle.razorpay.payment.processor.strategy;

import com.codishuttle.razorpay.common.util.RandomizerUtil;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codishuttle.razorpay.payment.processor.PaymentProcessor;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;

public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        //Call the third party API to process the payment and return the response

        //we are mocking the bank here

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("BANK").toString() : null;

        //simulation
        if (BANK_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("BANK_REJECTED", "Banked rejected the transaction registration");
        }

        String processorRef = "NBK_PROCESSOR" + RandomizerUtil.randomBase64(16);

//        String redirectRef = "http://REDIRECT_BANK.COM/" + processorRef;

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
