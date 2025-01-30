package codesumn.sboot.order.processor.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING("pending"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String value;

    public static OrderStatus fromValue(String value) {
        for (OrderStatus role : OrderStatus.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return PROCESSING;
    }
}
