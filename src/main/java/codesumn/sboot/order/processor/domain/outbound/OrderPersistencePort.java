package codesumn.sboot.order.processor.domain.outbound;

import codesumn.sboot.order.processor.domain.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderPersistencePort {
    Page<OrderModel> findAll(String searchTerm, Pageable pageable);

    Optional<OrderModel> findById(UUID id);

    Optional<OrderModel> findByOrderHash(String orderHash);

    OrderModel saveOrder(OrderModel orderModel);

    void deleteOrder(OrderModel orderModel);
}
