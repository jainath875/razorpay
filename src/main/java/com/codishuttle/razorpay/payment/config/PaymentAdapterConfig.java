package com.codishuttle.razorpay.payment.config;

import com.codishuttle.razorpay.common.enums.PaymentMethod;
import com.codishuttle.razorpay.payment.gateway.PaymentAdapter;
import com.codishuttle.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.codishuttle.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.codishuttle.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> putPaymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NETBANKING, netBankingAdapter,
                PaymentMethod.UPI, upiPaymentAdapter
        );
    }

}
