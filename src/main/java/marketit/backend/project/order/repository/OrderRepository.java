package marketit.backend.project.order.repository;

import lombok.RequiredArgsConstructor;
import marketit.backend.project.order.domain.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void saveOrder(Order order) {
        em.persist(order);
    }

    public Order findOrder(Long id) {
        return em.createQuery(
                "select o from Order o join fetch o.customer where o.orderId = :id", Order.class)
                .setParameter("id", id)
                .getResultList().get(0);
    }

    public List<Order> findOrders(int offset, int limit) {
        return em.createQuery(
                "select o from Order o join fetch o.customer", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
