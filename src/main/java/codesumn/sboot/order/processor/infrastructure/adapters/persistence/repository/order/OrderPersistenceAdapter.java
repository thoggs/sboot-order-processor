package codesumn.sboot.order.processor.infrastructure.adapters.persistence.repository.order;

import codesumn.sboot.order.processor.domain.models.OrderModel;
import codesumn.sboot.order.processor.domain.outbound.OrderPersistencePort;
import codesumn.sboot.order.processor.infrastructure.adapters.persistence.specifications.OrderSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderPersistenceAdapter implements OrderPersistencePort {

    final OrderJpaRepository orderJpaRepository;

    public OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Page<OrderModel> findAll(String searchTerm, Pageable pageable) {
        Specification<OrderModel> spec = (searchTerm != null && !searchTerm.isEmpty())
                ? OrderSpecifications.searchWithTerm(searchTerm)
                : null;

        return orderJpaRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<OrderModel> findById(UUID id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public Optional<OrderModel> findByOrderHash(String orderHash) {
        return orderJpaRepository.findByOrderHash(orderHash);
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        orderJpaRepository.save(orderModel);
        return orderModel;
    }

    @Override
    public void deleteOrder(OrderModel orderModel) {
        orderJpaRepository.delete(orderModel);
    }
}
