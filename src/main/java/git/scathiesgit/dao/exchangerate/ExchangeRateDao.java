package git.scathiesgit.dao.exchangerate;

import git.scathiesgit.dto.ExchangeRateData;
import git.scathiesgit.entity.ExchangeRate;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface ExchangeRateDao {

    List<ExchangeRate> findAll();

    Optional<ExchangeRate> findByCurrencyIDs(ExchangeRateData data);

    OptionalInt save(ExchangeRateData data);

    OptionalInt update(ExchangeRateData data);
}
