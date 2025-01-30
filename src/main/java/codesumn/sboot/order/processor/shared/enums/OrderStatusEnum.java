package codesumn.sboot.order.processor.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    PENDING("pending"),
    PROCESSING("processing"),
    PROCESSED("processed"),
    CANCELED("canceled");

    private final String value;

    public static OrderStatusEnum fromValue(String value) {
        for (OrderStatusEnum role : OrderStatusEnum.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return PROCESSING;
    }
}
