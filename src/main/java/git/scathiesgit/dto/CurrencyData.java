package git.scathiesgit.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyData {
    private String code;
    private String fullName;
    private String sign;
}
