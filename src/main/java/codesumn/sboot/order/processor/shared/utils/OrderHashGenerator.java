package codesumn.sboot.order.processor.shared.utils;

import codesumn.sboot.order.processor.domain.models.OrderItemModel;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHashGenerator {
    public static String generateOrderHash(List<OrderItemModel> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.stream()
                .sorted(Comparator.comparing(OrderItemModel::getProductName)
                        .thenComparing(OrderItemModel::getQuantity))
                .map(item -> item.getProductName() + ":" + item.getQuantity())
                .collect(Collectors.joining(","));
    }
}
