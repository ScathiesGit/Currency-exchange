package exchanger.repository;

import exchanger.entity.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository {

    int save(ExchangeRate rate);

    boolean update(ExchangeRate rate);

    boolean delete(int id);

    Optional<ExchangeRate> findByCurrency(int baseId, int targetId);

    List<ExchangeRate> findAll();
}
