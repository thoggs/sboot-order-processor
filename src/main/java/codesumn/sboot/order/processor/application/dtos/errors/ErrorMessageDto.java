package codesumn.sboot.order.processor.application.dtos.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorMessageDto {
    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("field")
    private String field;

    @JsonCreator
    public void ErrorMessage(
            @JsonProperty("errorCode") String errorCode,
            @JsonProperty("errorMessage") String errorMessage,
            @JsonProperty("field") String field
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.field = field;
    }

    public ErrorMessageDto(String errorCode, String errorMessage, String field) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.field = field;
    }

}
