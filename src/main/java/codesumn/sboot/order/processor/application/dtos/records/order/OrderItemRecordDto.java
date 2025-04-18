package codesumn.sboot.order.processor.application.dtos.records.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRecordDto(
        @NotBlank UUID id,
        @NotBlank String productCode,
        @NotBlank String productName,
        @NotNull @Min(1) Integer quantity,
        @NotNull @Min(0) BigDecimal unitPrice
) {
}
