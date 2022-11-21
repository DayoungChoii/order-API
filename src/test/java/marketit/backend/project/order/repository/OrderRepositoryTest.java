package marketit.backend.project.order.repository;

import marketit.backend.project.customer.domain.Customer;
import marketit.backend.project.customer.domain.CustomerStatus;
import marketit.backend.project.customer.repository.CustomerRepository;
import marketit.backend.project.order.domain.Order;
import marketit.backend.project.order.domain.OrderProduct;
import marketit.backend.project.order.domain.OrderStatus;
import marketit.backend.project.product.domain.Product;
import marketit.backend.project.product.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void 주문_저장() throws Exception{
        //given
        Customer customer = createCustomer();
        Product product = createProduct();
        Long orderId = 100L;
        marketit.backend.project.order.domain.Order order = buildOrder(customer, orderId);
        OrderProduct orderProduct = buildOrderProduct(product);
        order.addOrderProduct(orderProduct);

        //when
        orderRepository.saveOrder(order);

        //then
        Order foundOrder = orderRepository.findOrder(orderId);
        Assertions.assertThat(foundOrder.getOrderId()).isEqualTo(orderId);

    }

    private OrderProduct buildOrderProduct(Product product) {
        return OrderProduct.builder()
                .product(product)
                .orderPrice(10000)
                .orderCount(2)
                .build();
    }

    private Order buildOrder(Customer customer, Long orderId) {
        return Order.builder()
                .orderId(orderId)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ACCEPT)
                .customer(customer)
                .orderProducts(new ArrayList<>())
                .build();
    }


    private Product createProduct() {
        Product usb = Product.builder()
                .name("usb")
                .price(10000)
                .build();
        em.persist(usb);
        return usb;
    }

    private Customer createCustomer() {
        Customer customer = Customer.builder()
                .email("cc@ccc.com")
                .phoneNumber("3333-3333")
                .customerStatus(CustomerStatus.IN)
                .addresses(new ArrayList<>())
                .regDate(LocalDateTime.now())
                .build();
        em.persist(customer);
        return customer;
    }
}