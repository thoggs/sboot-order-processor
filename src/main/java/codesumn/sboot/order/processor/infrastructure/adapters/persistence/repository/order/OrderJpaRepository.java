package codesumn.sboot.order.processor.infrastructure.adapters.persistence.repository.order;

import codesumn.sboot.order.processor.domain.models.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderModel, UUID>, JpaSpecificationExecutor<OrderModel> {
    Optional<OrderModel> findByOrderHash(String orderHash);
}
