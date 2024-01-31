package exchanger.service;

import exchanger.entity.Currency;
import exchanger.repository.CurrencyRepository;
import exchanger.repository.JdbcCurrencyRepository;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyRepository currencyRepo = new JdbcCurrencyRepository();

    int save(Currency currency) {
        return currencyRepo.save(currency);
    }

    boolean delete(int id) {
        return currencyRepo.delete(id);
    }

    Optional<Currency> findById(int id) {
        return currencyRepo.findById(id);
    }

    Optional<Currency> findByCode(String code) {
        return currencyRepo.findByCode(code);
    }

    List<Currency> findAll() {
        return currencyRepo.findAll();
    }
}
