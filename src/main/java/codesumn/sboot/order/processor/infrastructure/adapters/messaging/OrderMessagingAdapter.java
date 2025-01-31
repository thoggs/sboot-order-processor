package codesumn.sboot.order.processor.infrastructure.adapters.messaging;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.processor.domain.outbound.OrderMessagingPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagingAdapter implements OrderMessagingPort {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;
    private final ObjectMapper objectMapper;

    public OrderMessagingAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${spring.rabbitmq.order.exchange}") String exchange,
            @Value("${spring.rabbitmq.order.routing.key}") String routingKey,
            ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendOrder(OrderRecordDto order) {
        try {
            byte[] messageBytes = objectMapper.writeValueAsBytes(order);
            Message message = new Message(messageBytes);
            rabbitTemplate.send(exchange, routingKey, message);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar mensagem", e);
        }
    }
}