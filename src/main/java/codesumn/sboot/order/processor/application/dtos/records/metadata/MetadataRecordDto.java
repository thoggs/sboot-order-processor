package codesumn.sboot.order.processor.application.dtos.records.metadata;

import codesumn.sboot.order.processor.application.dtos.errors.ErrorMessageDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record MetadataRecordDto(@JsonProperty("messages") List<ErrorMessageDto> messages) implements Serializable {

    @JsonCreator
    public MetadataRecordDto {
    }
}
