package codesumn.sboot.order.processor.infrastructure.adapters.persistence.specifications;

import codesumn.sboot.order.processor.domain.models.OrderModel;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {
    public static Specification<OrderModel> searchWithTerm(String searchTerm) {

        return (root, query, cb) -> {

            String likePattern = "%" + searchTerm.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("customerName")), likePattern),
                    cb.like(cb.lower(root.get("orderStatus")), likePattern),
                    cb.like(cb.function("CAST", String.class, root.get("totalPrice")), likePattern)
            );
        };
    }
}
