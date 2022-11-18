package marketit.backend.project.order.domain;

import marketit.backend.project.product.domain.Product;

public class OrderProduct {

    Long orderProductId;

    Order order;

    Product product;

    int orderPrice;

    int orderCount;
}
