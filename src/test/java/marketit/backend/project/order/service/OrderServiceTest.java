package marketit.backend.project.order.service;

import marketit.backend.project.customer.domain.Customer;
import marketit.backend.project.customer.domain.CustomerStatus;
import marketit.backend.project.customer.repository.CustomerRepository;
import marketit.backend.project.order.domain.Order;
import marketit.backend.project.order.domain.OrderProduct;
import marketit.backend.project.order.domain.OrderStatus;
import marketit.backend.project.order.dto.GetOrderInfoResponse;
import marketit.backend.project.order.dto.GetOrdersInfoDto;
import marketit.backend.project.order.dto.OrderAcceptDto;
import marketit.backend.project.order.dto.OrderAcceptResponse;
import marketit.backend.project.order.exception.OutOfQuantityException;
import marketit.backend.project.order.exception.WrongPriceException;
import marketit.backend.project.order.repository.OrderRepository;
import marketit.backend.project.product.domain.Inventory;
import marketit.backend.project.product.domain.Product;
import marketit.backend.project.product.domain.Warehouse;
import marketit.backend.project.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;


    @Test
    public void 주문_저장_성공() throws Exception {
        //given
        Long orderId = 1L;
        Long customerId = 1L;
        OrderAcceptDto orderAcceptDto1 = new OrderAcceptDto(1L, 1000000, 2);
        OrderAcceptDto orderAcceptDto2 = new OrderAcceptDto(2L, 200000, 2);
        List<OrderAcceptDto> orderAcceptDtos = new ArrayList<>();
        orderAcceptDtos.add(orderAcceptDto1);
        orderAcceptDtos.add(orderAcceptDto2);

        //창고 정보
        Warehouse warehouseA = new Warehouse(1L, "창고A", "서울");
        Warehouse warehouseB = new Warehouse(1L, "창고B", "부산");

        //재고 정보
        Inventory inventoryForMonitorWareHouseA = createInventory(1L, 5, warehouseA);
        Inventory inventoryForMonitorWareHouseB = createInventory(2L, 5, warehouseB);
        Inventory inventoryForKeyBoard = createInventory(3L, 5, warehouseA);

        //상품 정보
        Product monitor = createProduct(1L, "monitor", 1000000);
        Product keyboard = createProduct(2L, "keyboard", 200000);

        //재고 정보에 상품정보 추가
        inventoryForMonitorWareHouseA.addProduct(monitor);
        inventoryForMonitorWareHouseB.addProduct(monitor);
        inventoryForKeyBoard.addProduct(keyboard);

        doReturn(monitor).when(productRepository).findProduct(1L);
        doReturn(keyboard).when(productRepository).findProduct(2L);

        //사용자 생성
        Customer customerA = createCustomer("aa@aaa.com", "1111-1111");
        doReturn(customerA).when(customerRepository).findCustomer(customerId);
        doNothing().when(orderRepository).saveOrder(any(Order.class));


        //when
        OrderAcceptResponse orderAcceptResponse = orderService.saveOrder(orderId, customerId, orderAcceptDtos);


        //then
        assertThat(orderAcceptResponse.getOrderId()).isEqualTo(orderId);
        assertThat(orderAcceptResponse.getOrderStatus()).isEqualTo(OrderStatus.ACCEPT);
        assertThat(inventoryForMonitorWareHouseA.getQuantity()).isEqualTo(3); //5개 중 3개 주
    }

    private Inventory createInventory(long inventoryId, int quantity, Warehouse warehouse) {
        return Inventory.builder()
                .inventoryId(inventoryId)
                .quantity(quantity)
                .warehouse(warehouse)
                .build();
    }

    private Product createProduct(long productId, String name, int price) {
        return Product.builder()
                .productId(productId)
                .name(name)
                .price(price)
                .inventories(new ArrayList<>())
                .build();
    }

    private OrderProduct createOrderProduct(Product product, int orderPrice, int orderCount) {
        return OrderProduct.builder()
                .product(product)
                .orderPrice(orderPrice)
                .orderCount(orderCount)
                .build();
    }

    private Customer createCustomer(String email, String phoneNumber) {
        return Customer.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .customerStatus(CustomerStatus.IN)
                .addresses(new ArrayList<>())
                .regDate(LocalDateTime.now())
                .build();
    }

    @Test
    public void 주문_저장_실패_수량_안맞는_경우() throws Exception{
        //given
        Long orderId = 1L;
        Long customerId = 1L;
        OrderAcceptDto orderAcceptDto1 = new OrderAcceptDto(1L, 1000000, 2);
        OrderAcceptDto orderAcceptDto2 = new OrderAcceptDto(2L, 200000, 6);
        List<OrderAcceptDto> orderAcceptDtos = new ArrayList<>();
        orderAcceptDtos.add(orderAcceptDto1);
        orderAcceptDtos.add(orderAcceptDto2);

        //창고 정보
        Warehouse warehouseA = new Warehouse(1L, "창고A", "서울");
        Warehouse warehouseB = new Warehouse(1L, "창고B", "부산");

        //재고 정보
        Inventory inventoryForMonitorWareHouseA = createInventory(1L, 5, warehouseA);
        Inventory inventoryForMonitorWareHouseB = createInventory(2L, 5, warehouseB);
        Inventory inventoryForKeyBoard = createInventory(3L, 5, warehouseA);

        //상품 정보
        Product monitor = createProduct(1L, "monitor", 1000000);
        Product keyboard = createProduct(2L, "keyboard", 200000);

        //재고 정보에 상품정보 추가
        inventoryForMonitorWareHouseA.addProduct(monitor);
        inventoryForMonitorWareHouseB.addProduct(monitor);
        inventoryForKeyBoard.addProduct(keyboard);

        doReturn(monitor).when(productRepository).findProduct(1L);
        doReturn(keyboard).when(productRepository).findProduct(2L);

        //when


        //then
        Assertions.assertThrows(OutOfQuantityException.class, () -> orderService.saveOrder(orderId, customerId, orderAcceptDtos));

    }

    @Test
    public void 주문_저장_실패_가격_안맞는_경우() throws Exception{
        //given
        Long orderId = 1L;
        Long customerId = 1L;
        OrderAcceptDto orderAcceptDto1 = new OrderAcceptDto(1L, 100000, 2);
        OrderAcceptDto orderAcceptDto2 = new OrderAcceptDto(2L, 200000, 6);
        List<OrderAcceptDto> orderAcceptDtos = new ArrayList<>();
        orderAcceptDtos.add(orderAcceptDto1);
        orderAcceptDtos.add(orderAcceptDto2);

        //창고 정보
        Warehouse warehouseA = new Warehouse(1L, "창고A", "서울");
        Warehouse warehouseB = new Warehouse(1L, "창고B", "부산");

        //재고 정보
        Inventory inventoryForMonitorWareHouseA = createInventory(1L, 5, warehouseA);
        Inventory inventoryForMonitorWareHouseB = createInventory(2L, 5, warehouseB);
        Inventory inventoryForKeyBoard = createInventory(3L, 5, warehouseA);

        //상품 정보
        Product monitor = createProduct(1L, "monitor", 1000000);
        Product keyboard = createProduct(2L, "keyboard", 200000);

        //재고 정보에 상품정보 추가
        inventoryForMonitorWareHouseA.addProduct(monitor);
        inventoryForMonitorWareHouseB.addProduct(monitor);
        inventoryForKeyBoard.addProduct(keyboard);

        doReturn(monitor).when(productRepository).findProduct(1L);
        doReturn(keyboard).when(productRepository).findProduct(2L);

        //when


        //then
        Assertions.assertThrows(WrongPriceException.class, () -> orderService.saveOrder(orderId, customerId, orderAcceptDtos));

    }

    @Test
    public void 주문_완료_성공() throws Exception{
        //given
        Long orderId = 1L;
        Order foundOrder = Order.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.ACCEPT)
                .build();
        doReturn(foundOrder).when(orderRepository).findOrder(orderId);

        //when
        Order order = orderService.updateOrderStatus(orderId);

        //then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE);

    }

    @Test
    public void 주문_단건_조회() throws Exception{
        //given
        Long orderId = 1L;
        Product monitor = createProduct(1L, "monitor", 1000000);
        Product keyboard = createProduct(2L, "keyboard", 200000);

        OrderProduct orderProductMonitor = createOrderProduct(monitor, 1000000, 2);
        OrderProduct orderProductKeyboard = createOrderProduct(keyboard, 200000, 2);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProductMonitor);
        orderProducts.add(orderProductKeyboard);

        Order order = Order.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.ACCEPT)
                .orderProducts(orderProducts)
                .orderDate(LocalDateTime.now())
                .totalPrice(2400000)
                .build();
        doReturn(order).when(orderRepository).findOrder(orderId);
        //when
        GetOrderInfoResponse getOrderInfoResponse = orderService.findOrder(orderId);

        //then
        assertThat(getOrderInfoResponse.getOrderId()).isEqualTo(orderId);
        assertThat(getOrderInfoResponse.getOrderStatus()).isEqualTo(OrderStatus.ACCEPT);
        assertThat(getOrderInfoResponse.getOrderProductDtos().size()).isEqualTo(orderProducts.size());

    }

    @Test
    public void 주문_여러건_조회() throws Exception{
        //given
        Long orderId1 = 1L;
        Long orderId2 = 2L;

        Order order1 = Order.builder()
                .orderId(orderId1)
                .orderStatus(OrderStatus.ACCEPT)
                .orderDate(LocalDateTime.now())
                .totalPrice(2400000)
                .build();
        Order order2 = Order.builder()
                .orderId(orderId2)
                .orderStatus(OrderStatus.ACCEPT)
                .orderDate(LocalDateTime.now())
                .totalPrice(3000000)
                .build();
        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        doReturn(orders).when(orderRepository).findOrders(anyInt(), anyInt());
        //when
        List<GetOrdersInfoDto> getOrdersInfoDtos = orderService.findOrders(0, 100);

        //then
        assertThat(getOrdersInfoDtos.size()).isEqualTo(orders.size());

    }

}