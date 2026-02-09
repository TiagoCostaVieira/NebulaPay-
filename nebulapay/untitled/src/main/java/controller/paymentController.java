package controller;


import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;


@Slf4j
@RequetController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class paymentController{
    private final PaymentOrchestrator paymentOrchestrator;


    @postMapping("/payments")
    public ResponseEntity<PaymentResponse> createPayment(
            @requestHeader("Idempotency-Key") String idempotencyKey,
            @requestBody PaymentRequest request){

        log.info("Initiating payment creation. Idempotency-Key: {}",
                maskIdempotency(idempotencyKey));

        String effectiveIdempotency = idempotencyKey != null ? idempotencyKey : generateIdempotencyKey();


        return PaymentController.process(idempotencyKey, process);
    }

}