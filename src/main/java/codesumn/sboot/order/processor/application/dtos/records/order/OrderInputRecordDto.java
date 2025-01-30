package codesumn.sboot.order.processor.application.dtos.records.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record OrderInputRecordDto(
        @NotBlank String customerCode,
        @NotBlank String customerName,
        BigDecimal totalPrice,
        @NotNull String orderStatus,
        @NotNull @Size(min = 1) List<OrderItemRecordDto> items
) {
}
