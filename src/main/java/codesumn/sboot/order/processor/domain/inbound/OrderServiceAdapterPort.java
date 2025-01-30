package codesumn.sboot.order.processor.domain.inbound;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderInputRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.pagination.PaginationResponseDto;
import codesumn.sboot.order.processor.application.dtos.records.response.ResponseDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrderServiceAdapterPort {
    PaginationResponseDto<List<OrderRecordDto>> findAll(
            int page,
            int pageSize,
            String searchTerm,
            String sorting
    ) throws IOException;

    ResponseDto<OrderRecordDto> getOrderById(UUID id);

    ResponseDto<OrderRecordDto> createOrder(OrderInputRecordDto orderInputRecordDto);

    ResponseDto<OrderRecordDto> updateOrder(UUID id, OrderInputRecordDto orderInputRecordDto);

    ResponseDto<OrderRecordDto> deleteOrder(UUID id);
}
