package git.scathiesgit.service.action.currency;

import git.scathiesgit.dto.CurrencyData;
import git.scathiesgit.entity.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyActionService {

    Optional<Currency> findByCode(String code);

    Optional<Currency> findById(int id);

    List<Currency> findAll();

    int save(CurrencyData data);
}
