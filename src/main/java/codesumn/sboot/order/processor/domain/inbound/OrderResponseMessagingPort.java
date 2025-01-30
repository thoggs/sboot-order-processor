package codesumn.sboot.order.processor.domain.inbound;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;

public interface OrderResponseMessagingPort {
    void consumeOrderResponse(OrderRecordDto message);
}
