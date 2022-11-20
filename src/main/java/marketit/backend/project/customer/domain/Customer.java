package marketit.backend.project.customer.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id @GeneratedValue
    private Long customerId;

    private String email;

    private String phoneNumber;

    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    private LocalDateTime regDate;

    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;

    public void addAddress(Address address){
        addresses.add(address);
        address.setCustomer(this);
    }

    @Builder
    public Customer(Long customerId, String email, String phoneNumber, List<Address> addresses, LocalDateTime regDate, CustomerStatus customerStatus) {
        this.customerId = customerId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.addresses = addresses;
        this.regDate = regDate;
        this.customerStatus = customerStatus;
    }
}
