package marketit.backend.project.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import marketit.backend.project.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GetOrderInfoResponse {
    Long orderId;
    OrderStatus orderStatus;
    LocalDateTime orderDate;
    int totalPrice;
    List<OrderProductDto> orderProductDtos;

    @Builder
    public GetOrderInfoResponse(Long orderId, OrderStatus orderStatus, LocalDateTime orderDate, int totalPrice, List<OrderProductDto> orderProductDtos) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.orderProductDtos = orderProductDtos;
    }
}
