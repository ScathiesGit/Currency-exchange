package exchanger.service;

import exchanger.dto.ExchangeRateDto;
import exchanger.dto.ExchangeInfo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    public ExchangeServiceImpl(ExchangeRateService exchangeRateService, CurrencyService currencyService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
    }

    public Optional<ExchangeInfo> exchange(String baseCode, String targetCode, double amount) {
        ExchangeInfo result = null;
        Optional<BigDecimal> rate = findRate(baseCode, targetCode);
        if (rate.isPresent()) {
            var converted = rate.get().multiply(BigDecimal.valueOf(amount), MathContext.DECIMAL32);
            result = ExchangeInfo.builder()
                    .baseCurrency(currencyService.findByCode(baseCode).get())
                    .targetCurrency(currencyService.findByCode(targetCode).get())
                    .rate(rate.get())
                    .amount(amount)
                    .convertedAmount(converted)
                    .build();
        }
        return Optional.ofNullable(result);
    }

    private Optional<BigDecimal> findRate(String baseCode, String targetCode) {
        BigDecimal result;
        var directRate = exchangeRateService.findByCurrency(baseCode, targetCode);
        if (directRate.isPresent()) {
            return directRate.map(ExchangeRateDto::getRate);
        }

        var reverseRate = exchangeRateService.findByCurrency(targetCode, baseCode);
        if (reverseRate.isPresent()) {
            result = BigDecimal.ONE.divide(reverseRate.get().getRate(), MathContext.DECIMAL64);
            return Optional.of(result);
        }

        var usd = currencyService.findByCode("USD");
        if (usd.isPresent()) {
            var usdToBase = exchangeRateService.findByCurrency(usd.get().getCode(), baseCode);
            var usdToTarget = exchangeRateService.findByCurrency(usd.get().getCode(), targetCode);
            if (usdToBase.isPresent() && usdToTarget.isPresent()) {
                result = usdToTarget.get().getRate().divide(
                        usdToBase.get().getRate(), MathContext.DECIMAL32
                );
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }
}
