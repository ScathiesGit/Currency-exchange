package git.scathiesgit.dao;

import git.scathiesgit.entity.Currency;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface CurrencyDao {

    Optional<Currency> findByCode(String code);

    Optional<Currency> findById(int id);

    List<Currency> findAll();

    int save(Currency currency);
}
