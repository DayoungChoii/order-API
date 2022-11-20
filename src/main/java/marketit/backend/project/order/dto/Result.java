package marketit.backend.project.order.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Result<T> {
    private T data;
    private Boolean success;
    private String error;
}
