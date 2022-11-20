package marketit.backend.project.customer.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id @GeneratedValue
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String streetAddress;

    private String addressDetail;

    private String zipcode;

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Builder
    public Address(Long addressId, Customer customer, String streetAddress, String addressDetail, String zipcode) {
        this.addressId = addressId;
        this.customer = customer;
        this.streetAddress = streetAddress;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
    }
}
