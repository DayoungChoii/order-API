package marketit.backend.project.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id @GeneratedValue
    Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    Warehouse warehouse;

    int quantity;

    public void addProduct(Product product){
        if(this.product != null){
            this.product.getInventories().remove(this);
        }
        this.product = product;
        product.getInventories().add(this);
    }

    public void changeQuantity(int newQuantity){
        this.quantity = newQuantity;
    }

    @Builder
    public Inventory(Long inventoryId, Product product, Warehouse warehouse, int quantity) {
        this.inventoryId = inventoryId;
        this.product = product;
        this.warehouse = warehouse;
        this.quantity = quantity;
    }
}
