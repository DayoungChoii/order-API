package marketit.backend.project.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import marketit.backend.project.product.domain.Product;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {

    @Id @GeneratedValue
    Long orderProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    int orderPrice;

    int orderCount;

    public void setOrder(Order order) {
        this.order = order;
    }

    @Builder
    public OrderProduct(Long orderProductId, Order order, Product product, int orderPrice, int orderCount) {
        this.orderProductId = orderProductId;
        this.order = order;
        this.product = product;
        this.orderPrice = orderPrice;
        this.orderCount = orderCount;
    }
}
