package exchanger.repository;

import exchanger.entity.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {

    int save(Currency currency);

    boolean delete(int id);

    Optional<Currency> findById(int id);

    Optional<Currency> findByCode(String code);

    List<Currency> findAll();
}
