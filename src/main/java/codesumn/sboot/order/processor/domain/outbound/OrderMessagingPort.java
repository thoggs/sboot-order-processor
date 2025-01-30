package codesumn.sboot.order.processor.domain.outbound;

import codesumn.sboot.order.processor.domain.models.OrderModel;

public interface OrderMessagingPort {
    void sendOrder(OrderModel order);
}
