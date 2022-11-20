package marketit.backend.project.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id @GeneratedValue
    Long productId;

    String name;

    String description;

    int price;

    @OneToMany(mappedBy = "product")
    List<Inventory> inventories;

    @Builder
    public Product(Long productId, String name, String description, int price, List<Inventory> inventories) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.inventories = inventories;
    }
}
