package exchanger.service;

import exchanger.dto.ExchangeRateDto;
import exchanger.entity.Currency;
import exchanger.entity.ExchangeRate;
import exchanger.repository.CurrencyRepository;
import exchanger.repository.ExchangeRateRepository;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final CurrencyRepository currencyRepo;

    private final ExchangeRateRepository exchangeRateRepo;

    public ExchangeRateServiceImpl(CurrencyRepository currencyRepo, ExchangeRateRepository exchangeRateRepo) {
        this.currencyRepo = currencyRepo;
        this.exchangeRateRepo = exchangeRateRepo;
    }

    public Optional<ExchangeRateDto> save(String baseCode, String targetCode, BigDecimal rate) {
        ExchangeRateDto result = null;
        var id = -1;
        var baseCurrency = currencyRepo.findByCode(baseCode);
        var targetCurrency = currencyRepo.findByCode(targetCode);

        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            var exchangeRate = ExchangeRate.builder()
                    .baseCurrencyId(baseCurrency.get().getId())
                    .targetCurrencyId(targetCurrency.get().getId())
                    .rate(rate)
                    .build();
            id = exchangeRateRepo.save(exchangeRate);
            if (id > 0) {
                exchangeRate.setId(id);
                result = toDto(exchangeRate, baseCurrency.get(), targetCurrency.get());
            }
        }
        return Optional.ofNullable(result);
    }

    public Optional<ExchangeRateDto> update(String baseCode, String targetCode, BigDecimal rate) {
        ExchangeRateDto result = null;
        var baseCurrency = currencyRepo.findByCode(baseCode);
        var targetCurrency = currencyRepo.findByCode(targetCode);

        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            var toUpdate = ExchangeRate.builder()
                    .baseCurrencyId(baseCurrency.get().getId())
                    .targetCurrencyId(targetCurrency.get().getId())
                    .rate(rate)
                    .build();

            if (exchangeRateRepo.update(toUpdate)) {
                var exchangeRate = exchangeRateRepo.findByCurrency(
                        baseCurrency.get().getId(), targetCurrency.get().getId()
                ).get();
                result = toDto(exchangeRate, baseCurrency.get(), targetCurrency.get());
            }
        }
        return Optional.ofNullable(result);
    }

    public Optional<ExchangeRateDto> findByCurrency(String baseCode, String targetCode) {
        Optional<ExchangeRateDto> result = Optional.empty();
        var baseCurrency = currencyRepo.findByCode(baseCode);
        var targetCurrency = currencyRepo.findByCode(targetCode);

        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            result = exchangeRateRepo.findByCurrency(
                    baseCurrency.get().getId(), targetCurrency.get().getId()
                ).map(exchangeRate -> toDto(exchangeRate, baseCurrency.get(), targetCurrency.get()));
        }
        return result;
    }

    public List<ExchangeRateDto> findAll() {
        return exchangeRateRepo.findAll().stream()
                .map(exchangeRate -> ExchangeRateDto.builder()
                        .id(exchangeRate.getId())
                        .baseCurrency(currencyRepo.findById(exchangeRate.getBaseCurrencyId()).get())
                        .targetCurrency(currencyRepo.findById(exchangeRate.getTargetCurrencyId()).get())
                        .rate(exchangeRate.getRate()).build()
                )
                .toList();
    }

    private ExchangeRateDto toDto(ExchangeRate exchangeRate, Currency base, Currency target) {
        return ExchangeRateDto.builder()
                .id(exchangeRate.getId())
                .baseCurrency(base)
                .targetCurrency(target)
                .rate(exchangeRate.getRate())
                .build();
    }
}
