package codesumn.sboot.order.processor.application.dtos.records.order;

import codesumn.sboot.order.processor.shared.enums.OrderStatusEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderRecordDto(
        @NotNull UUID id,
        @NotBlank String customerName,
        @NotNull OrderStatusEnum orderStatus,
        @Min(0) BigDecimal totalPrice,
        @NotNull List<OrderItemRecordDto> items
) {
}