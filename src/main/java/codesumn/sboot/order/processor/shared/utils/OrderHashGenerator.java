package codesumn.sboot.order.processor.shared.utils;

import codesumn.sboot.order.processor.domain.models.OrderItemModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;

public class OrderHashGenerator {

    public static String generateOrderHash(String customerCode, List<OrderItemModel> items) {
        if (customerCode == null || items == null || items.isEmpty()) {
            return null;
        }

        StringBuilder rawHash = new StringBuilder(customerCode).append(":");

        items.stream()
                .sorted(Comparator
                        .comparing(OrderItemModel::getProductCode, Comparator.nullsFirst(String::compareTo))
                        .thenComparing(item -> item.getQuantity() == null ? 0 : item.getQuantity()))
                .forEach(item -> rawHash
                        .append(item.getProductCode())
                        .append(":")
                        .append(item.getQuantity() == null ? 0 : item.getQuantity())
                        .append(","));

        if (rawHash.charAt(rawHash.length() - 1) == ',') {
            rawHash.setLength(rawHash.length() - 1);
        }

        return sha256(rawHash.toString());
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error on generate hash SHA-256", e);
        }
    }
}