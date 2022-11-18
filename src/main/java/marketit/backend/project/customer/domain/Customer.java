package marketit.backend.project.customer.domain;

import java.time.LocalDateTime;

public class Customer {

    Long customerId;

    String email;

    String phoneNumber;

    LocalDateTime regDate;

    CustomerStatus customerStatus;

}
