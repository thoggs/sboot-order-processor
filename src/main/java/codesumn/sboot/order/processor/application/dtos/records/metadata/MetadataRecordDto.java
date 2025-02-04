package codesumn.sboot.order.processor.application.dtos.records.metadata;

import codesumn.sboot.order.processor.application.dtos.errors.ErrorMessageDto;

import java.io.Serializable;
import java.util.List;

public record MetadataRecordDto(List<ErrorMessageDto> messages) implements Serializable {
}
