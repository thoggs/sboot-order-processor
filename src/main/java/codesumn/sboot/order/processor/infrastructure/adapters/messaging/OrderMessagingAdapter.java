package codesumn.sboot.order.processor.infrastructure.adapters.messaging;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.processor.domain.outbound.OrderMessagingPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagingAdapter implements OrderMessagingPort {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public OrderMessagingAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${spring.rabbitmq.order.exchange}") String exchange,
            @Value("${spring.rabbitmq.order.routing.key}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void sendOrder(OrderRecordDto order) {
        rabbitTemplate.convertAndSend(exchange, routingKey, order);
    }
}
