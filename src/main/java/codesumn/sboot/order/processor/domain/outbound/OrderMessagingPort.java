package codesumn.sboot.order.processor.domain.outbound;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;

public interface OrderMessagingPort {
    void sendOrder(OrderRecordDto order);
}
