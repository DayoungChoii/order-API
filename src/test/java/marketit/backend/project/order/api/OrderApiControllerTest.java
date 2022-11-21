package marketit.backend.project.order.api;

import com.google.gson.Gson;
import marketit.backend.project.order.domain.Order;
import marketit.backend.project.order.domain.OrderProduct;
import marketit.backend.project.order.domain.OrderStatus;
import marketit.backend.project.order.dto.*;
import marketit.backend.project.order.exception.OutOfQuantityException;
import marketit.backend.project.order.exception.WrongPriceException;
import marketit.backend.project.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderApiControllerTest {

    @InjectMocks
    private OrderApiController orderApiController;

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderApiController).build();
    }

    @Test
    public void 주문_접수_처리_성공() throws Exception{
        //given
        Long orderId = 1L;
        Long customerId = 1L;

        OrderAcceptDto orderAcceptDto1 = new OrderAcceptDto(1L, 200000, 2);
        OrderAcceptDto orderAcceptDto2 = new OrderAcceptDto(2L, 50000, 3);
        List<OrderAcceptDto> orderAcceptDtos = new ArrayList<>();
        orderAcceptDtos.add(orderAcceptDto1);
        orderAcceptDtos.add(orderAcceptDto2);

        OrderAcceptRequest orderAcceptRequest = new OrderAcceptRequest();
        orderAcceptRequest.setCustomerId(customerId);
        orderAcceptRequest.setOrderAcceptDtos(orderAcceptDtos);

        OrderAcceptResponse orderAcceptResponse = OrderAcceptResponse.builder()
                .orderId(orderId)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ACCEPT)
                .totalPrice(550000)
                .build();
        doReturn(orderAcceptResponse).when(orderService).saveOrder(anyLong(), anyLong(), anyList());

        //when
        Gson gson = new Gson();

        ResultActions result = mockMvc.perform(post("/api/orders/" + orderId + "/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(orderAcceptRequest))
        ).andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.orderStatus").value(OrderStatus.ACCEPT.toString()))
                .andExpect(jsonPath("$.data.totalPrice").value(550000))
                .andExpect(jsonPath("$.success").value(true));

    }

    @Test
    public void 주문_접수_처리_실패_유효성검증() throws Exception{
        //given
        Long orderId = 1L;
        Long customerId = 1L;

        OrderAcceptDto orderAcceptDto1 = new OrderAcceptDto(null, 200000, 2);
        OrderAcceptDto orderAcceptDto2 = new OrderAcceptDto(2L, 50000, 3);
        List<OrderAcceptDto> orderAcceptDtos = new ArrayList<>();
        orderAcceptDtos.add(orderAcceptDto1);
        orderAcceptDtos.add(orderAcceptDto2);

        OrderAcceptRequest orderAcceptRequest = new OrderAcceptRequest();
        orderAcceptRequest.setCustomerId(customerId);
        orderAcceptRequest.setOrderAcceptDtos(orderAcceptDtos);

        //when
        Gson gson = new Gson();

        ResultActions result = mockMvc.perform(post("/api/orders/" + orderId + "/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(orderAcceptRequest))
        ).andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").isNotEmpty());


    }

    @Test
    public void 주문_접수_처리_실패_수량_모자름() throws Exception{
        //given
        Long orderId = 1L;
        Long customerId = 1L;

        OrderAcceptDto orderAcceptDto1 = new OrderAcceptDto(1L, 200000, 2);
        OrderAcceptDto orderAcceptDto2 = new OrderAcceptDto(2L, 50000, 3);
        List<OrderAcceptDto> orderAcceptDtos = new ArrayList<>();
        orderAcceptDtos.add(orderAcceptDto1);
        orderAcceptDtos.add(orderAcceptDto2);

        OrderAcceptRequest orderAcceptRequest = new OrderAcceptRequest();
        orderAcceptRequest.setCustomerId(customerId);
        orderAcceptRequest.setOrderAcceptDtos(orderAcceptDtos);

        doThrow(OutOfQuantityException.class).when(orderService).saveOrder(anyLong(), anyLong(), anyList());

        //when
        Gson gson = new Gson();

        ResultActions result = mockMvc.perform(post("/api/orders/" + orderId + "/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(orderAcceptRequest))
        ).andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));

    }

    @Test
    public void 주문_접수_처리_실패_가격이상() throws Exception{
        //given
        Long orderId = 1L;
        Long customerId = 1L;

        OrderAcceptDto orderAcceptDto1 = new OrderAcceptDto(1L, 200000, 2);
        OrderAcceptDto orderAcceptDto2 = new OrderAcceptDto(2L, 50000, 3);
        List<OrderAcceptDto> orderAcceptDtos = new ArrayList<>();
        orderAcceptDtos.add(orderAcceptDto1);
        orderAcceptDtos.add(orderAcceptDto2);

        OrderAcceptRequest orderAcceptRequest = new OrderAcceptRequest();
        orderAcceptRequest.setCustomerId(customerId);
        orderAcceptRequest.setOrderAcceptDtos(orderAcceptDtos);

        doThrow(WrongPriceException.class).when(orderService).saveOrder(anyLong(), anyLong(), anyList());

        //when
        Gson gson = new Gson();

        ResultActions result = mockMvc.perform(post("/api/orders/" + orderId + "/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(orderAcceptRequest))
        ).andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));

    }

    @Test
    public void 주문_왼료_성공() throws Exception{
        //given
        Long orderId = 1L;
        doReturn(null).when(orderService).updateOrderStatus(anyLong());

        //when
        ResultActions result = mockMvc.perform(patch("/api/orders/" + orderId + "/complete"))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

    }

    @Test
    public void 주문_왼료_실패() throws Exception{
        //given
        Long orderId = 1L;
        doThrow(RuntimeException.class).when(orderService).updateOrderStatus(anyLong());

        //when
        ResultActions result = mockMvc.perform(patch("/api/orders/" + orderId + "/complete"))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));

    }

    @Test
    public void 단일_주문_조회() throws Exception{
        //given
        Long orderId = 1L;

        OrderProductDto orderProductDto1 = OrderProductDto.builder()
                .productName("keyboard")
                .price(200000)
                .count(2)
                .build();

        OrderProductDto orderProductDto2 = OrderProductDto.builder()
                .productName("mouse")
                .price(50000)
                .count(3)
                .build();
        List<OrderProductDto> orderProductDtos = new ArrayList<>();
        orderProductDtos.add(orderProductDto1);
        orderProductDtos.add(orderProductDto2);

        GetOrderInfoResponse getOrderInfoResponse = GetOrderInfoResponse.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.ACCEPT)
                .orderDate(LocalDateTime.now())
                .totalPrice(550000)
                .orderProductDtos(orderProductDtos)
                .build();

        doReturn(getOrderInfoResponse).when(orderService).findOrder(anyLong());

        //when
        ResultActions result = mockMvc.perform(get("/api/orders/" + orderId)
        ).andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.orderStatus").value(OrderStatus.ACCEPT.toString()))
                .andExpect(jsonPath("$.data.orderProductDtos").isArray())
                .andExpect(jsonPath("$.data.totalPrice").value(550000))
                .andExpect(jsonPath("$.success").value(true));

    }

    @Test
    public void 주문_목록_조회() throws Exception{
        //given
        GetOrdersInfoDto ordersInfoDto1 = GetOrdersInfoDto.builder()
                .orderId(1L)
                .totalPrice(50000)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ACCEPT)
                .build();

        GetOrdersInfoDto ordersInfoDto2 = GetOrdersInfoDto.builder()
                .orderId(2L)
                .totalPrice(100000)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ACCEPT)
                .build();

        List<GetOrdersInfoDto> getOrdersInfoDtos = new ArrayList<>();
        getOrdersInfoDtos.add(ordersInfoDto1);
        getOrdersInfoDtos.add(ordersInfoDto2);

        doReturn(getOrdersInfoDtos).when(orderService).findOrders(anyInt(), anyInt());

        //when
        Gson gson = new Gson();

        ResultActions result = mockMvc.perform(get("/api/orders/")
                .content(gson.toJson(new GetOrdersInfoRequest(0, 100)))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());



        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.success").value(true));

    }


}