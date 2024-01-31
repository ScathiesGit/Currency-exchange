package exchanger.service;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeService {

    Optional<BigDecimal> exchange(String baseCode, String targetCode, double amount);
}
