package codesumn.sboot.order.processor.application.dtos.records.errors;

import codesumn.sboot.order.processor.application.dtos.records.metadata.MetadataRecordDto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record ErrorResponseDto<T>(T data, Boolean success, List<MetadataRecordDto> metadata) implements Serializable {

    public static ErrorResponseDto<List<Object>> createWithoutData(List<MetadataRecordDto> metadata) {
        return new ErrorResponseDto<>(Collections.emptyList(), false, metadata);
    }

    @Override
    public String toString() {
        return "ErrorResponseDto{" +
                "data=" + data +
                ", success=" + success +
                ", metadata=" + metadata +
                '}';
    }
}