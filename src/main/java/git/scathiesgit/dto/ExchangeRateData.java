package git.scathiesgit.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateData {
    private int baseCurrencyId;
    private int targetCurrencyId;
    private BigDecimal rate;
}
