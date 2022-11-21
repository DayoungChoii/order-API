package marketit.backend.project;

import lombok.RequiredArgsConstructor;
import marketit.backend.project.customer.domain.Address;
import marketit.backend.project.customer.domain.Customer;
import marketit.backend.project.customer.domain.CustomerStatus;
import marketit.backend.project.product.domain.Inventory;
import marketit.backend.project.product.domain.Product;
import marketit.backend.project.product.domain.Warehouse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.createMemberData();
        initService.createProductData();
    }

    @RequiredArgsConstructor
    @Component
    @Transactional
    static class InitService {

        private final EntityManager em;

        /**
         * member A
         *  address 집
         *  address 회사
         *
         * member B
         *  address 집
         *  address 회사
         */
        public void createMemberData() {

            Address addressAHome = Address.builder()
                    .streetAddress("가시 가구 가동")
                    .addressDetail("101호")
                    .zipcode("11111").build();

            Address addressACompany = Address.builder()
                    .streetAddress("나시 나구 나동")
                    .addressDetail("202호")
                    .zipcode("22222").build();

            Address addressBHome = Address.builder()
                    .streetAddress("다시 다구 다동")
                    .addressDetail("303호")
                    .zipcode("33333").build();

            Address addressBCompany = Address.builder()
                    .streetAddress("라시 라구 라동")
                    .addressDetail("404호")
                    .zipcode("44444").build();

            Customer customerA = Customer.builder()
                    .email("aa@aaa.com")
                    .phoneNumber("1111-1111")
                    .customerStatus(CustomerStatus.IN)
                    .addresses(new ArrayList<>())
                    .regDate(LocalDateTime.now())
                    .build();

            Customer customerB = Customer.builder()
                    .email("bb@bbb.com")
                    .phoneNumber("2222-2222")
                    .customerStatus(CustomerStatus.IN)
                    .addresses(new ArrayList<>())
                    .regDate(LocalDateTime.now())
                    .build();

            customerA.addAddress(addressAHome);
            customerA.addAddress(addressACompany);
            customerB.addAddress(addressBHome);
            customerB.addAddress(addressBCompany);

            em.persist(addressAHome);
            em.persist(addressACompany);
            em.persist(addressBHome);
            em.persist(addressBCompany);

            em.persist(customerA);
            em.persist(customerB);
        }

        /**
         * 창고A
         * 창고B
         *
         * 키보드
         * 모니터
         * 마우스
         *
         * 키보드
         *  창고A 2개
         *  창고b 3개
         * 모니터
         *  창고A 10개
         * 마우스
         *  창고B 20개
         *
         */
        public void createProductData() {
            Warehouse warehouseA = Warehouse.builder()
                    .name("창고A")
                    .location("서울")
                    .build();
            Warehouse warehouseB = Warehouse.builder()
                    .name("창고B")
                    .location("부산")
                    .build();

            Product keyboard = Product.builder()
                    .name("keyboard")
                    .price(200000)
                    .build();

            Product monitor = Product.builder()
                    .name("monitor")
                    .price(1000000)
                    .build();

            Product mouse = Product.builder()
                    .name("mouse")
                    .price(50000)
                    .build();

            em.persist(warehouseA);
            em.persist(warehouseB);
            em.persist(keyboard);
            em.persist(monitor);
            em.persist(mouse);

            Inventory inventory1 = Inventory.builder()
                    .product(keyboard)
                    .warehouse(warehouseA)
                    .quantity(2)
                    .build();

            Inventory inventory2 = Inventory.builder()
                    .product(keyboard)
                    .warehouse(warehouseB)
                    .quantity(3)
                    .build();

            Inventory inventory3 = Inventory.builder()
                    .product(monitor)
                    .warehouse(warehouseA)
                    .quantity(10)
                    .build();

            Inventory inventory4 = Inventory.builder()
                    .product(mouse)
                    .warehouse(warehouseB)
                    .quantity(20)
                    .build();

            em.persist(inventory1);
            em.persist(inventory2);
            em.persist(inventory3);
            em.persist(inventory4);

        }
    }

}
