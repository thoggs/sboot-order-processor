package codesumn.sboot.order.processor.application.adapters.inbound;

import codesumn.sboot.order.processor.application.dtos.records.metadata.MetadataPaginationRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderInputRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderItemRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.pagination.PaginationDto;
import codesumn.sboot.order.processor.application.dtos.records.pagination.PaginationResponseDto;
import codesumn.sboot.order.processor.application.dtos.records.response.ResponseDto;
import codesumn.sboot.order.processor.application.mappers.OrderMapper;
import codesumn.sboot.order.processor.domain.inbound.OrderServiceAdapterPort;
import codesumn.sboot.order.processor.domain.models.OrderItemModel;
import codesumn.sboot.order.processor.domain.models.OrderModel;
import codesumn.sboot.order.processor.domain.outbound.OrderMessagingPort;
import codesumn.sboot.order.processor.domain.outbound.OrderPersistencePort;
import codesumn.sboot.order.processor.shared.enums.OrderStatusEnum;
import codesumn.sboot.order.processor.shared.exceptions.errors.DuplicateOrderException;
import codesumn.sboot.order.processor.shared.exceptions.errors.ResourceNotFoundException;
import codesumn.sboot.order.processor.shared.parsers.SortParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceAdapter implements OrderServiceAdapterPort {
    private final OrderPersistencePort orderPersistencePort;
    private final OrderMessagingPort orderMessagingPort;
    private final SortParser sortParser;

    @Autowired
    public OrderServiceAdapter(
            OrderPersistencePort orderPersistencePort, OrderMessagingPort orderMessagingPort,
            SortParser sortParser
    ) {
        this.orderPersistencePort = orderPersistencePort;
        this.orderMessagingPort = orderMessagingPort;
        this.sortParser = sortParser;
    }

    @Override
    public PaginationResponseDto<List<OrderRecordDto>> findAll(
            int page,
            int pageSize,
            String searchTerm,
            String sorting
    ) throws IOException {
        String decodedSorting = (sorting != null && !sorting.trim().isEmpty() && !"[]".equals(sorting))
                ? URLDecoder.decode(sorting, StandardCharsets.UTF_8)
                : null;

        Sort sort = (decodedSorting != null && !decodedSorting.trim().isEmpty() && !"[]".equals(decodedSorting))
                ? sortParser.parseSorting(decodedSorting)
                : Sort.by(Sort.Order.asc("customerName"));

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<OrderModel> orderPage = orderPersistencePort.findAll(searchTerm, pageable);

        List<OrderRecordDto> orderRecords = orderPage
                .getContent()
                .stream()
                .map(this::convertToOrderRecordDto)
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(
                orderPage.getNumber() + 1,
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );

        MetadataPaginationRecordDto metadata = new MetadataPaginationRecordDto(
                pagination,
                Collections.emptyList()
        );

        return PaginationResponseDto.create(orderRecords, metadata);
    }

    @Override
    public ResponseDto<OrderRecordDto> getOrderById(UUID id) {
        OrderModel order = orderPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        OrderRecordDto userRecord = convertToOrderRecordDto(order);

        return ResponseDto.create(userRecord);
    }

    @Override
    public ResponseDto<OrderRecordDto> createOrder(OrderInputRecordDto orderInputRecordDto) {
        OrderModel order = OrderMapper.fromDto(orderInputRecordDto);

        if (orderPersistencePort.findByOrderHash(order.getOrderHash()).isPresent()) {
            throw new DuplicateOrderException();
        }

        OrderModel savedOrder = orderPersistencePort.saveOrder(order);

        OrderRecordDto savedOrderRecord = convertToOrderRecordDto(savedOrder);

        orderMessagingPort.sendOrder(savedOrderRecord);

        return ResponseDto.create(convertToOrderRecordDto(savedOrder));
    }

    @Override
    public ResponseDto<OrderRecordDto> updateOrder(UUID id, OrderInputRecordDto orderInput) {
        OrderModel existingOrder = orderPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (orderPersistencePort.findByOrderHash(existingOrder.getOrderHash()).isPresent()) {
            throw new DuplicateOrderException();
        }

        existingOrder.setCustomerName(orderInput.customerName());
        existingOrder.setItems(convertOrderItemsToModel(orderInput.items(), existingOrder));
        existingOrder.setTotalPrice(orderInput.totalPrice());
        existingOrder.setOrderStatus(OrderStatusEnum.fromValue(orderInput.orderStatus()));

        orderPersistencePort.saveOrder(existingOrder);

        OrderRecordDto updatedOrderRecord = convertToOrderRecordDto(existingOrder);

        return ResponseDto.create(updatedOrderRecord);
    }

    @Override
    public ResponseDto<OrderRecordDto> deleteOrder(UUID id) {
        OrderModel order = orderPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        orderPersistencePort.deleteOrder(order);

        OrderRecordDto orderRecordDto = convertToOrderRecordDto(order);

        return ResponseDto.create(orderRecordDto);
    }

    private OrderRecordDto convertToOrderRecordDto(OrderModel order) {
        return new OrderRecordDto(
                order.getId(),
                order.getCustomerName(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                convertOrderItems(order.getItems())
        );
    }

    private List<OrderItemRecordDto> convertOrderItems(List<OrderItemModel> items) {
        return items.stream()
                .map(item -> new OrderItemRecordDto(
                        item.getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .collect(Collectors.toList());
    }

    private List<OrderItemModel> convertOrderItemsToModel(List<OrderItemRecordDto> items, OrderModel order) {
        return items.stream()
                .map(item -> new OrderItemModel(
                        UUID.randomUUID(),
                        order,
                        item.productName(),
                        item.quantity(),
                        item.unitPrice()
                ))
                .collect(Collectors.toList());
    }
}
