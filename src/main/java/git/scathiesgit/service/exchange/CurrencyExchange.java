package git.scathiesgit.service;

import git.scathiesgit.dto.ExchangeData;

import java.math.BigDecimal;
import java.util.Optional;

public interface CurrencyExchange {

    Optional<BigDecimal> exchange(ExchangeData data);
}
