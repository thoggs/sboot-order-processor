package codesumn.sboot.order.processor.domain.inbound;

public interface OrderResponseMessagingPort {
    void consumeOrderResponse(String message);
}
