package marketit.backend.project.order.dto;

import lombok.Builder;
import lombok.Getter;
import marketit.backend.project.order.domain.OrderStatus;

import java.time.LocalDateTime;

@Getter
public class OrderAcceptResponse {
    Long orderId;
    OrderStatus orderStatus;
    LocalDateTime orderDate;
    int totalPrice;

    @Builder
    public OrderAcceptResponse(Long orderId, OrderStatus orderStatus, LocalDateTime orderDate, int totalPrice) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }
}