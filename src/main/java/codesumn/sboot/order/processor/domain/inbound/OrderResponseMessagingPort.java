package codesumn.sboot.order.processor.domain.inbound;

public interface OrderResponseMessagingPort {
    void consumeOrderResponse(byte[] message);
}
