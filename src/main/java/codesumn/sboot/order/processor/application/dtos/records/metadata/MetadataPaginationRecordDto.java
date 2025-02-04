package codesumn.sboot.order.processor.application.dtos.records.metadata;

import codesumn.sboot.order.processor.application.dtos.errors.ErrorMessageDto;
import codesumn.sboot.order.processor.application.dtos.records.pagination.PaginationDto;

import java.io.Serializable;
import java.util.List;

public record MetadataPaginationRecordDto(
        PaginationDto pagination,
        List<ErrorMessageDto> messages
) implements Serializable {
}
