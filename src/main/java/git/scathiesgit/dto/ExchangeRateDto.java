package git.scathiesgit.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ExchangeRateDto {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}
