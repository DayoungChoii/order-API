package marketit.backend.project.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Warehouse {

    @Id @GeneratedValue
    Long warehouseId;

    String name;

    String Location;

    @Builder
    public Warehouse(Long warehouseId, String name, String location) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.Location = location;
    }
}
