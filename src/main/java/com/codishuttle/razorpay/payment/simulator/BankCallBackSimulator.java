package com.codishuttle.razorpay.payment.simulator;

import com.codishuttle.razorpay.common.enums.ChaosMode;
import com.codishuttle.razorpay.common.enums.PaymentMethod;
import com.codishuttle.razorpay.common.enums.PaymentStatus;
import com.codishuttle.razorpay.common.util.RandomizerUtil;
import com.codishuttle.razorpay.payment.entity.Payment;
import com.codishuttle.razorpay.payment.repository.PaymentRepository;
import com.codishuttle.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallBackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

//    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks() {

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);

        log.info("Simulating payments for {} payments", candidates.size());

        if (candidates.isEmpty()) {
            return;
        }

        for (Payment payment : candidates) {
            simulateCallback(payment);
        }

    }



    private void simulateCallback(Payment payment) {

        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());

        LocalDateTime dueAt = dueAt(payment, methodConfig);

        if(LocalDateTime.now().isBefore(dueAt)) {
            return;
        }

        ChaosMode chaosMode = simulatorConfig.getChaosMode();

        switch (chaosMode) {
            case SUCCESS -> resolve(payment, true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT -> {
                log.debug("BankCallBack simulator: Payment Timed out");
            }
            case NORMAL, SLOW -> resolve(payment, shouldApprove(payment, methodConfig));
        }

    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodConfig.getSuccessRate();
    }

    private void resolve(Payment payment, boolean approved) {
        if (approved) {
            String bankRef = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null, null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Decline");
        }
    }


    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int range = methodConfig.getMaxDelaySeconds() - methodConfig.getMinDelaySeconds();

        int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range + 1);

        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW) {
            delaySeconds *= 2;

        }

        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }

}
