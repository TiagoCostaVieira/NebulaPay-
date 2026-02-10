package controller;

import dto.CreatePaymentResponse;
import Security.MaskingPatternLayout;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.http.ResponseEntity;
import com.stripe.exception.IdempotencyException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class paymentController{
    private final PaymentOrchestrator paymentOrchestrator;


    @PostMapping("/payments")
    public ResponseEntity<ProblemDetail> createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PaymentRequest request){

        log.info("Initiating payment creation. Idempotency-Key: {}",
                maskIdempotency(idempotencyKey));

        String effectiveKey = idempotencyKey != null ? idempotencyKey : generateIdempotencyKey();

        try{
            CreatePaymentResponse response = paymentOrchestrator.createPayment(request, effectiveIdempotency);
            log.info("Payment successfully created. PaymentId: {}", response.paymentId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IdempotencyException e){
            log.warn("Idempotence conflict detected. Key: {}", MaskingPatternLayout.maskIdempotencyKey(effectiveKey));
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