package controller;
import com.stripe.exception.IdempotencyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;


@Slf4j
@RequetController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class paymentController{
    private final PaymentOrchestrator paymentOrchestrator;


    @postMapping("/payments")
    public ResponseEntity<ProblemDetail> createPayment(
            @requestHeader("Idempotency-Key") String idempotencyKey,
            @requestBody PaymentRequest request){

        log.info("Initiating payment creation. Idempotency-Key: {}",
                maskIdempotency(idempotencyKey));

        String effectiveIdempotency = idempotencyKey != null ? idempotencyKey : generateIdempotencyKey();

        try{
            createPaymentResponse response = paymentOrchestrator.createPayment(request, effectiveIdempotency);
            log.info("Payment successfully created. PaymentId: {}", response.paymentId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IdempotencyException e){
            log.warn("Idempotence conflict detected. Key: {}", maskIdempotencyKey(effectiveKey));

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ProblemDetail.forStatusAndDetail(
                            HttpStatus.CONFLICT,
                            "Payment already processed with this idempotence key."
                    ));


        } catch (InvalidPaymentRequestException e){
            log.warn("Invalid payment request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid payment details: " + e.getMessage()));
        }


        return PaymentController.process(idempotencyKey, process);
    }

    private String generateIdempotencyKey(){
        return UUID.randomUUID().toString();
    }

}