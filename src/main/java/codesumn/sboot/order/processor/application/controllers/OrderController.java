package codesumn.sboot.order.processor.application.controllers;

import codesumn.sboot.order.processor.application.dtos.records.order.OrderInputRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.processor.application.dtos.records.pagination.PaginationResponseDto;
import codesumn.sboot.order.processor.application.dtos.records.response.ResponseDto;
import codesumn.sboot.order.processor.application.dtos.param.FilterCriteriaParamDto;
import codesumn.sboot.order.processor.domain.inbound.OrderServiceAdapterPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderServiceAdapterPort orderServicePort;

    public OrderController(OrderServiceAdapterPort orderServicePort) {
        this.orderServicePort = orderServicePort;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<List<OrderRecordDto>>> index(
            @Valid @ParameterObject @ModelAttribute FilterCriteriaParamDto parameters
    ) throws IOException {
        return new ResponseEntity<>(orderServicePort.findAll(
                parameters.getPage(),
                parameters.getPageSize(),
                parameters.getSearchTerm(),
                parameters.getSortField()
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<OrderRecordDto>> show(@PathVariable UUID id) {
        return new ResponseEntity<>(orderServicePort.getOrderById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDto<OrderRecordDto>> store(
            @RequestBody @Valid OrderInputRecordDto orderInputRecordDto
    ) {
        return new ResponseEntity<>(orderServicePort.createOrder(orderInputRecordDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<OrderRecordDto>> update(
            @PathVariable UUID id,
            @RequestBody @Valid OrderInputRecordDto orderInputRecordDto
    ) {
        return new ResponseEntity<>(orderServicePort.updateOrder(id, orderInputRecordDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<OrderRecordDto>> destroy(@PathVariable UUID id) {
        return new ResponseEntity<>(orderServicePort.deleteOrder(id), HttpStatus.OK);
    }
}
