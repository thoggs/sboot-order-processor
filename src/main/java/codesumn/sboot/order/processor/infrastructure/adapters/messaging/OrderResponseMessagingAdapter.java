package codesumn.sboot.order.processor.infrastructure.adapters.messaging;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderInputRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.processor.domain.inbound.OrderResponseMessagingPort;
import codesumn.sboot.order.processor.domain.inbound.OrderServiceAdapterPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMessagingAdapter implements OrderResponseMessagingPort {

    private final OrderServiceAdapterPort orderServiceAdapterPort;
    private final ObjectMapper objectMapper;

    public OrderResponseMessagingAdapter(OrderServiceAdapterPort orderServiceAdapterPort, ObjectMapper objectMapper) {
        this.orderServiceAdapterPort = orderServiceAdapterPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.processor.response.queue}")
    public void consumeOrderResponse(byte[] messageBytes) {
        try {
            OrderRecordDto order = objectMapper.readValue(messageBytes, OrderRecordDto.class);

            OrderInputRecordDto orderInputRecordDto = new OrderInputRecordDto(
                    order.customerCode(),
                    order.customerName(),
                    order.totalPrice(),
                    order.orderStatus().name(),
                    order.items()
            );

            orderServiceAdapterPort.updateOrderSafely(order.id(), orderInputRecordDto);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar mensagem", e);
        }
    }
}