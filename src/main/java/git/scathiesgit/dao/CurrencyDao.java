package git.scathiesgit.dao;

import git.scathiesgit.entity.Currency;

import java.util.Optional;

public interface CurrencyDao extends Dao<Currency> {

    Optional<Currency> findByCode(String code);

    Optional<Currency> findById(int id);
}
