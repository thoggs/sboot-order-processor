package codesumn.sboot.order.processor.infrastructure.adapters.messaging;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderInputRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.processor.domain.inbound.OrderResponseMessagingPort;
import codesumn.sboot.order.processor.domain.inbound.OrderServiceAdapterPort;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMessagingAdapter implements OrderResponseMessagingPort {

    private final OrderServiceAdapterPort orderServiceAdapterPort;

    @Autowired
    public OrderResponseMessagingAdapter(OrderServiceAdapterPort orderServiceAdapterPort) {
        this.orderServiceAdapterPort = orderServiceAdapterPort;
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.processor.response.queue}")
    public void consumeOrderResponse(OrderRecordDto order) {
        OrderInputRecordDto orderInputRecordDto = new OrderInputRecordDto(
                order.customerName(),
                order.totalPrice(),
                order.orderStatus().name(),
                order.items()
        );

        orderServiceAdapterPort.updateOrder(order.id(), orderInputRecordDto);
    }
}
