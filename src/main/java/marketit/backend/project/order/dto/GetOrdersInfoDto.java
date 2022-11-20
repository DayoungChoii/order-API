package marketit.backend.project.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import marketit.backend.project.order.domain.OrderStatus;
import marketit.backend.project.order.service.OrderService;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetOrdersInfoDto {
    private Long orderId;

    private int totalPrice;

    private OrderStatus orderStatus;

    private LocalDateTime orderDate;


    @Builder

    public GetOrdersInfoDto(Long orderId, int totalPrice, OrderStatus orderStatus, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
    }
}
