package codesumn.sboot.order.processor.infrastructure.adapters.messaging;

import codesumn.sboot.order.processor.application.events.OrderCreatedEvent;
import codesumn.sboot.order.processor.domain.outbound.OrderMessagingPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

    private final OrderMessagingPort orderMessagingPort;

    public OrderEventListener(OrderMessagingPort orderMessagingPort) {
        this.orderMessagingPort = orderMessagingPort;
    }

    @TransactionalEventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        orderMessagingPort.sendOrder(event.order());
    }
}
