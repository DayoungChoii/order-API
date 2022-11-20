package marketit.backend.project.customer.repository;

import lombok.RequiredArgsConstructor;
import marketit.backend.project.customer.domain.Customer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Repository
public class CustomerRepository {

    private final EntityManager em;

    public Customer findCustomer(Long customerId) {
        return em.find(Customer.class, customerId);
    }
}
