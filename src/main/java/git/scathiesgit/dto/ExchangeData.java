package git.scathiesgit.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeData {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private double amount;
}
