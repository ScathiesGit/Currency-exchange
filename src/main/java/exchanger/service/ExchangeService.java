package exchanger.service;

import exchanger.entity.ExchangeRate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

public class ExchangeService {

    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    private final CurrencyService currencyService = new CurrencyService();

    public Optional<BigDecimal> exchange(String baseCode, String targetCode, double amount) {
        BigDecimal result = null;
        Optional<BigDecimal> rate = findRate(baseCode, targetCode);
        if (rate.isPresent()) {
            result = rate.get().multiply(BigDecimal.valueOf(amount), MathContext.DECIMAL64);
        }
        return Optional.ofNullable(result);
    }

    private Optional<BigDecimal> findRate(String baseCode, String targetCode) {
        BigDecimal result = null;

        var directRate = exchangeRateService.findByCurrency(baseCode, targetCode);
        if (directRate.isPresent()) {
            return directRate.map(ExchangeRate::getRate);
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
                        usdToBase.get().getRate(), MathContext.DECIMAL64
                );
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }
}
