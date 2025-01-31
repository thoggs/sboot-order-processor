package codesumn.sboot.order.processor.application.events;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;

public record OrderCreatedEvent(OrderRecordDto order) {
}
