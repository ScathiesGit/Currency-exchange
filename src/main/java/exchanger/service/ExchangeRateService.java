package exchanger.service;

import exchanger.entity.ExchangeRate;
import exchanger.repository.CurrencyRepository;
import exchanger.repository.ExchangeRateRepository;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private final CurrencyRepository currencyRepo = new JdbcCurrencyRepository();

    private final ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository();

    int save(String baseCode, String targetCode, BigDecimal rate) {
        var generatedId = -1;
        var baseCurrency = currencyRepo.findByCode(baseCode);
        var targetCurrency = currencyRepo.findByCode(targetCode);

        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            generatedId = exchangeRateRepository.save(ExchangeRate.builder()
                    .baseCurrencyId(baseCurrency.get().getId())
                    .targetCurrencyId(targetCurrency.get().getId())
                    .rate(rate)
                    .build()
            );
        }
        return generatedId;
    }

    boolean update(String baseCode, String targetCode, BigDecimal rate) {
        var isUpdated = false;
        var baseCurrency = currencyRepo.findByCode(baseCode);
        var targetCurrency = currencyRepo.findByCode(targetCode);

        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
             isUpdated = exchangeRateRepository.update(ExchangeRate.builder()
                    .baseCurrencyId(baseCurrency.get().getId())
                    .targetCurrencyId(targetCurrency.get().getId())
                    .rate(rate)
                    .build()
            );
        }
        return isUpdated;
    }

    Optional<ExchangeRate> findByCurrency(String baseCode, String targetCode) {
        Optional<ExchangeRate> result = Optional.empty();
        var baseCurrency = currencyRepo.findByCode(baseCode);
        var targetCurrency = currencyRepo.findByCode(targetCode);

        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            result = exchangeRateRepository.findByCurrency(
                    baseCurrency.get().getId(), targetCurrency.get().getId()
            );
        }
        return result;
    }

    List<ExchangeRate> findAll() {
        return exchangeRateRepository.findAll();
    }
}
