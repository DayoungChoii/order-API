package marketit.backend.project.order.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketit.backend.project.order.dto.*;
import marketit.backend.project.order.exception.OutOfQuantityException;
import marketit.backend.project.order.exception.WrongPriceException;
import marketit.backend.project.order.service.OrderService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Slf4j
public class OrderApiController {

    private final OrderService orderService;

    /**
     * 주문 접수 처리
     * @param id
     * @param orderAcceptRequest
     * @param bindingResult
     * @return
     */
    @PostMapping("/{id}/accept")
    public Result accept(@PathVariable Long id, @Valid @RequestBody OrderAcceptRequest orderAcceptRequest, BindingResult bindingResult){
        Result result = new Result();
        if(bindingResult.hasErrors()){
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("|"));
            result.setError(errorMessage);
            result.setSuccess(false);
            return result;
        }
        try{
            OrderAcceptResponse orderAcceptResponse = orderService.saveOrder(id, orderAcceptRequest.getCustomerId(), orderAcceptRequest.getOrderAcceptDtos());
            result.setData(orderAcceptResponse);
            result.setSuccess(true);
        } catch(OutOfQuantityException | WrongPriceException e){
            result.setError(e.getMessage());
            result.setSuccess(false);
        } catch (Exception e){
            log.info("ORDER ACCEPT FAIL: " + e);
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 주문 완료 처리
     * @param id
     * @return
     */
    @PatchMapping("/{id}/complete")
    public Result complete(@PathVariable Long id){

        Result result = new Result();
        try {
            orderService.updateOrderStatus(id);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            log.info("ORDER UPDATE FAIL: "+ e);
        }
        return result;
    }

    /**
     * 단일 주문 조회
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getOrderInfo(@PathVariable Long id){
        Result result = new Result();
        GetOrderInfoResponse getOrderInfoResponse =  orderService.findOrder(id);
        result.setData(getOrderInfoResponse);
        result.setSuccess(true);

        return result;
    }

    /**
     * 주문 목록 조회
     * @param getOrdersInfoRequest
     * @return
     */
    @GetMapping("")
    public Result getOrdersInfo(@RequestBody GetOrdersInfoRequest getOrdersInfoRequest){
        Result result = new Result();
        List<GetOrdersInfoDto> getOrdersInfoDtoList = orderService.findOrders(getOrdersInfoRequest.getOffset(), getOrdersInfoRequest.getLimit());
        result.setData(getOrdersInfoDtoList);
        result.setSuccess(true);

        return result;
    }

}
