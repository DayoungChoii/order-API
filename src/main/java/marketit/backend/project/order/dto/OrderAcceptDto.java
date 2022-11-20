package marketit.backend.project.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderAcceptDto {

    @NotNull(message = "productId는 필수 입니다")
    private Long productId;

    @NotNull(message = "orderPrice는 필수 입니다")
    private Integer orderPrice;

    @NotNull(message = "orderCount는 필수 입니다")
    private Integer orderCount;
}
