package marketit.backend.project.order.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class OrderAcceptRequest {

    private Long customerId;

    @Valid
    @NotNull
    private List<OrderAcceptDto> orderAcceptDtos;
}
