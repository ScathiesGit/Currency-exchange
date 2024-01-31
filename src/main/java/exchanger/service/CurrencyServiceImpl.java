package exchanger.service;

import exchanger.entity.Currency;
import exchanger.repository.CurrencyRepository;
import exchanger.repository.JdbcCurrencyRepository;

import java.util.List;
import java.util.Optional;

public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepo = new JdbcCurrencyRepository();

    public int save(Currency currency) {
        return currencyRepo.save(currency);
    }

    public boolean delete(int id) {
        return currencyRepo.delete(id);
    }

    public Optional<Currency> findById(int id) {
        return currencyRepo.findById(id);
    }

    public Optional<Currency> findByCode(String code) {
        return currencyRepo.findByCode(code);
    }

    public List<Currency> findAll() {
        return currencyRepo.findAll();
    }
}
