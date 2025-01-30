package codesumn.sboot.order.processor.application.mappers;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderInputRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderItemRecordDto;
import codesumn.sboot.order.processor.domain.models.OrderItemModel;
import codesumn.sboot.order.processor.domain.models.OrderModel;
import codesumn.sboot.order.processor.shared.enums.OrderStatusEnum;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderModel fromDto(OrderInputRecordDto dto) {
        OrderModel order = new OrderModel();
        order.setCustomerName(dto.customerName());
        order.setOrderStatus(OrderStatusEnum.fromValue(dto.orderStatus()));
        order.setItems(mapOrderItems(dto.items(), order));

        return order;
    }

    private static List<OrderItemModel> mapOrderItems(List<OrderItemRecordDto> items, OrderModel order) {
        return items.stream()
                .map(itemDto -> new OrderItemModel(
                        UUID.randomUUID(),
                        order,
                        itemDto.productName(),
                        itemDto.quantity(),
                        itemDto.unitPrice()
                ))
                .collect(Collectors.toList());
    }
}
