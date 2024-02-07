package exchanger.service;

import exchanger.entity.Currency;
import exchanger.repository.CurrencyRepository;
import exchanger.repository.JdbcCurrencyRepository;

import java.util.List;
import java.util.Optional;

public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepo;

    public static CurrencyServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

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

    private CurrencyServiceImpl(CurrencyRepository currencyRepo) {
        this.currencyRepo = currencyRepo;
    }

    private static class SingletonHolder {
        private static final CurrencyServiceImpl INSTANCE = new CurrencyServiceImpl(
                JdbcCurrencyRepository.getInstance()
        );
    }
}
