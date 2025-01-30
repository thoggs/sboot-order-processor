package codesumn.sboot.order.processor.application.mappers;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderInputRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderItemRecordDto;
import codesumn.sboot.order.processor.domain.models.OrderItemModel;
import codesumn.sboot.order.processor.domain.models.OrderModel;
import codesumn.sboot.order.processor.shared.enums.OrderStatusEnum;
import codesumn.sboot.order.processor.shared.utils.OrderHashGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderModel fromDto(OrderInputRecordDto dto) {
        OrderModel order = new OrderModel();
        order.setCustomerName(dto.customerName());
        order.setOrderStatus(OrderStatusEnum.fromValue(dto.orderStatus()));

        List<OrderItemModel> items = mapOrderItems(dto.items(), order);
        order.setItems(items);

        order.setOrderHash(OrderHashGenerator.generateOrderHash(items));

        return order;
    }

    private static List<OrderItemModel> mapOrderItems(
            List<OrderItemRecordDto> items,
            OrderModel order
    ) {
        return items.stream()
                .map(itemDto -> OrderItemModel.builder()
                        .order(order)
                        .productName(itemDto.productName())
                        .quantity(itemDto.quantity())
                        .price(itemDto.unitPrice())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
