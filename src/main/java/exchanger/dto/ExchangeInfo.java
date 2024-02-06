package exchanger.dto;

import exchanger.entity.Currency;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ExchangeInfo {

    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private double amount;
    private BigDecimal convertedAmount;
}
