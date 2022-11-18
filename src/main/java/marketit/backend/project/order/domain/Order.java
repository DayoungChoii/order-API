package marketit.backend.project.order.domain;

import marketit.backend.project.customer.domain.Customer;

import java.time.LocalDateTime;

public class Order {

    Long orderId;

    Customer customer;

    OrderProduct orderProduct;

    LocalDateTime orderDate;

    OrderStatus orderStatus;
}
