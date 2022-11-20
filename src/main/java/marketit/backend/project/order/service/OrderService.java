package marketit.backend.project.order.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import marketit.backend.project.customer.repository.CustomerRepository;
import marketit.backend.project.order.domain.Order;
import marketit.backend.project.order.domain.OrderProduct;
import marketit.backend.project.order.domain.OrderStatus;
import marketit.backend.project.order.dto.*;
import marketit.backend.project.order.exception.OutOfQuantityException;
import marketit.backend.project.order.exception.WrongPriceException;
import marketit.backend.project.order.repository.OrderRepository;
import marketit.backend.project.product.domain.Inventory;
import marketit.backend.project.product.domain.Product;
import marketit.backend.project.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    /**
     * 주문 접수 처리
     * @param orderId
     * @param customerId
     * @param orderAcceptDtos
     * @return
     */
    @Transactional
    public OrderAcceptResponse saveOrder(Long orderId, Long customerId, List<OrderAcceptDto> orderAcceptDtos) {
        //product 객체 찾아오기
        Map<Long, Product> productMap = createProductMap(orderAcceptDtos);

        //orderProduct 객체 만들기
        List<OrderProduct> orderProducts = createOrderProducts(orderAcceptDtos, productMap);

        //수량, 가격 안맞으면 예외 던지기
        priceAndCountValidateCheck(orderProducts);

        //주문한 수량 만큼 재고 감소
        changeQuantity(orderProducts);

        //order 객체 만들어서 저장
        Order order = createOrder(orderId, customerId, orderProducts);
        for (OrderProduct orderProduct : orderProducts){
            order.addOrderProduct(orderProduct);
        }

        orderRepository.saveOrder(order);
        return createOrderResponse(orderId, order);
    }

    /**
     * OrderResponse 생성
     * @param orderId
     * @param order
     * @return
     */
    private OrderAcceptResponse createOrderResponse(Long orderId, Order order) {
        return OrderAcceptResponse.builder()
                .orderId(orderId)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }

    /**
     * Order 생성
     * @param orderId
     * @param customerId
     * @param orderProducts
     * @return
     */
    private Order createOrder(Long orderId, Long customerId, List<OrderProduct> orderProducts) {
        return Order.builder()
                .orderId(orderId)
                .customer(customerRepository.findCustomer(customerId))
                .orderDate(LocalDateTime.now())
                .totalPrice(getTotalPrice(orderProducts))
                .orderProducts(new ArrayList<>())
                .orderStatus(OrderStatus.ACCEPT)
                .build();
    }

    /**
     * 주문한 상품 재고 변경
     * @param orderProducts
     */
    private void changeQuantity(List<OrderProduct> orderProducts) {
        for(OrderProduct orderProduct : orderProducts){
            List<Inventory> inventories = orderProduct.getProduct().getInventories();
            int orderCount = orderProduct.getOrderCount();

            for (Inventory inventory : inventories) {
                int quantity = inventory.getQuantity();
                int result = quantity - orderCount;

                if(result < 0 ){ //재고 수량 부족
                    result = 0;
                    orderCount = orderCount - quantity;
                } else{ //재고 수량 충분
                    orderCount = 0;
                }
                inventory.changeQuantity(result);
                if(orderCount == 0) break;
            }
        }
    }

    /**
     * 수량, 가격 유효성 체크
     * @param orderProducts
     */
    private void priceAndCountValidateCheck(List<OrderProduct> orderProducts) {
        for(OrderProduct orderProduct : orderProducts){
            Product product = orderProduct.getProduct();
            int orderPrice = orderProduct.getOrderPrice();
            int orderCount = orderProduct.getOrderCount();

            List<Inventory> inventories = product.getInventories();
            int currentQuantity = 0;
            for(Inventory inventory : inventories){
                currentQuantity += inventory.getQuantity();
            }

            if(currentQuantity - orderCount < 0){
                throw new OutOfQuantityException("남은 수량이 주문 수량 보다 적습니다.");
            }
            if(product.getPrice() != orderPrice){
                throw new WrongPriceException("주문한 물품의 금액과 물품의 금액이 일치하지 않습니다.");
            }
        }
    }

    /**
     * 주문 총 금액 계산
     * @param orderProducts
     * @return
     */
    private int getTotalPrice(List<OrderProduct> orderProducts) {
        int totalPrice = 0;
        for(OrderProduct orderProduct : orderProducts) {
            int orderPrice = orderProduct.getOrderPrice();
            int orderCount = orderProduct.getOrderCount();
            totalPrice += (orderCount * orderPrice);
        }
        return totalPrice;
    }

    /**
     * OrderProduct List 생성
     * @param orderAcceptDtos
     * @param productMap
     * @return
     */
    private List<OrderProduct> createOrderProducts(List<OrderAcceptDto> orderAcceptDtos, Map<Long, Product> productMap) {
        return orderAcceptDtos.stream()
                .map(oar -> OrderProduct.builder()
                        .orderPrice(oar.getOrderPrice())
                        .orderCount(oar.getOrderCount())
                        .product(productMap.get(oar.getProductId()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ProductMap 생성
     * @param orderAcceptDtos
     * @return
     */
    private Map<Long, Product> createProductMap(List<OrderAcceptDto> orderAcceptDtos) {
        return orderAcceptDtos.stream()
                .map(oar -> productRepository.findProduct(oar.getProductId()))
                .collect(Collectors.toMap(p -> p.getProductId(), p -> p));
    }

    /**
     * 주문 상태 변경
     * @param id
     * @return
     */
    @Transactional
    public Order updateOrderStatus(Long id) {
        Order order = orderRepository.findOrder(id);
        order.changeOrderStatus(OrderStatus.COMPLETE);
        return order;
    }

    /**
     * 주문 단건 조회
     * @param id
     * @return
     */
    public GetOrderInfoResponse findOrder(Long id) {
        Order order = orderRepository.findOrder(id);
        List<OrderProduct> orderProducts = order.getOrderProducts();
        List<OrderProductDto> orderProductDtos = orderProducts.stream()
                .map(op -> OrderProductDto.builder()
                        .productName(op.getProduct().getName())
                        .price(op.getOrderPrice())
                        .count(op.getOrderCount())
                        .build())
                .collect(Collectors.toList());

        GetOrderInfoResponse getOrderInfoResponse = GetOrderInfoResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .orderProductDtos(orderProductDtos)
                .totalPrice(order.getTotalPrice())
                .build();

        return getOrderInfoResponse;
    }

    /**
     * 주문 다건 조회
     * @param offset
     * @param limit
     * @return
     */
    public List<GetOrdersInfoDto> findOrders(int offset, int limit) {
        List<Order> orders = orderRepository.findOrders(offset,limit);
        List<GetOrdersInfoDto> getOrdersInfoDtos = orders.stream()
                                                    .map(o -> GetOrdersInfoDto.builder()
                                                            .orderId(o.getOrderId())
                                                            .orderDate(o.getOrderDate())
                                                            .totalPrice(o.getTotalPrice())
                                                            .orderStatus(o.getOrderStatus())
                                                            .build())
                                                    .collect(Collectors.toList());
        return getOrdersInfoDtos;

    }
}
