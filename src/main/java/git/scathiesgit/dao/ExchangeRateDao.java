package git.scathiesgit.dao;

import git.scathiesgit.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalInt;

public interface ExchangeRateDao extends Dao<ExchangeRate> {

    Optional<ExchangeRate> findByCurrencyIDs(ExchangeRate rate);

    OptionalInt update(ExchangeRate rate);

    Optional<BigDecimal> findExchangeRate(ExchangeRate rate);
}
