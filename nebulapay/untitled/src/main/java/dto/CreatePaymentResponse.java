package dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Payment creation response")
public class CreatePaymentResponse {
    @Schema
    private String paymentId;

    @Schema
    private String stripePaymentIntentId;

    @Schema
    private string status;

    @Schema
    private String customerId;

    @Schema
    private  String clientSecret;

    @Schema
    private long amount;

    @Schema
    private String currency;

}
