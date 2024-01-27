package git.scathiesgit.dao;

import git.scathiesgit.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface ExchangeRateDao {

    List<ExchangeRate> findAll();

    Optional<ExchangeRate> findByCurrencyIDs(ExchangeRate rate);

    OptionalInt save(ExchangeRate rate);

    OptionalInt update(ExchangeRate rate);
}
