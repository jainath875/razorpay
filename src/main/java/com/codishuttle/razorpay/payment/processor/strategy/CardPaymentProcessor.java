package com.codishuttle.razorpay.payment.processor.strategy;

import com.codishuttle.razorpay.common.util.RandomizerUtil;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codishuttle.razorpay.payment.processor.PaymentProcessor;
import com.codishuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    private static final String PAN_CARD_DECLINED = "4000000000000002";
    private static final String PAN_CARD_EXPIRED = "4000000000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        String pan = request.pan();

        if(PAN_CARD_DECLINED.equals(pan)){
            log.warn("Card declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "card declined by bank");
        }

        if(PAN_CARD_EXPIRED.equals(pan)){
            log.warn("Pan card has expired");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has expired");
        }

        String processorRef = "CARD_PROCESSOR" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
        
    }
}
