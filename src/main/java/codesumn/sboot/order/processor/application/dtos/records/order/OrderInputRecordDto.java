package codesumn.sboot.order.processor.application.dtos.records.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderInputRecordDto(
        @NotBlank String customerName,
        @NotBlank BigDecimal totalPrice,
        @NotNull String orderStatus,
        @NotBlank List<OrderItemRecordDto> items
) {
}
