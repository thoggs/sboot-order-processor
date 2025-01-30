package codesumn.sboot.order.processor.infrastructure.adapters.messaging;

import codesumn.sboot.order.processor.domain.inbound.OrderResponseMessagingPort;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMessagingAdapter implements OrderResponseMessagingPort {

    @Value("${spring.rabbitmq.processor.response.queue}")
    private String responseQueue;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.processor.response.queue}")
    public void consumeOrderResponse(String message) {
        System.out.println("📥 Mensagem recebida da fila '" + responseQueue + "': " + message);
        // TODO: Implementar lógica de processamento aqui...
    }
}