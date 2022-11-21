package marketit.backend.project.customer.repository;

import marketit.backend.project.customer.domain.Customer;
import marketit.backend.project.customer.domain.CustomerStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void 고객_찾기_단_건() throws Exception{
        //given
        Customer customer = Customer.builder()
                .email("cc@ccc.com")
                .phoneNumber("3333-3333")
                .customerStatus(CustomerStatus.IN)
                .addresses(new ArrayList<>())
                .regDate(LocalDateTime.now())
                .build();
        em.persist(customer);

        //when
        Customer foundCustomer = customerRepository.findCustomer(customer.getCustomerId());

        //then
        Assertions.assertThat(customer.getEmail()).isEqualTo("cc@ccc.com");
        Assertions.assertThat(customer.getPhoneNumber()).isEqualTo("3333-3333");

    }
}