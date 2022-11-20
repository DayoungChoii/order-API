package marketit.backend.project.order.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class OrderProductDto {
    String productName;
    int price;
    int count;

    @Builder
    public OrderProductDto(String productName, int price, int count) {
        this.productName = productName;
        this.price = price;
        this.count = count;
    }
}
