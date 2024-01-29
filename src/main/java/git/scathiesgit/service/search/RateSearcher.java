package git.scathiesgit.service.search;

import git.scathiesgit.dao.currency.CurrencyDao;
import git.scathiesgit.dao.currency.JdbcCurrency;
import git.scathiesgit.dao.exchangerate.ExchangeRateDao;
import git.scathiesgit.dao.exchangerate.JdbcExchangeRate;
import git.scathiesgit.dto.ExchangeRateData;
import git.scathiesgit.entity.ExchangeRate;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Getter
public class RateSearcher implements ExchangeRateSearcher {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRate();

    private final CurrencyDao currencyDao = new JdbcCurrency();

    @Override
    public Optional<BigDecimal> findRate(String baseCode, String targetCode) {
        Optional<BigDecimal> result = Optional.empty();
        var baseCurrency = currencyDao.findByCode(baseCode);
        var targetCurrency = currencyDao.findByCode(baseCode);
        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            var baseId = baseCurrency.get().getId();
            var targetId = targetCurrency.get().getId();
            var directRate = findByBaseAndTargetId(baseId, targetId);
            result = directRate.isPresent() ? directRate : findByBaseAndTargetId(targetId, baseId);
            if (result.isEmpty()) {
                result = findByRateThroughUsd(baseId, targetId);
            }
        }
        return result;
    }

    private Optional<BigDecimal> findByBaseAndTargetId(int baseId, int targetId) {
        BigDecimal result = null;
        var direct = createExchangeRateData(baseId, targetId);
        var directRate = exchangeRateDao.findByCurrencyIDs(direct).map(ExchangeRate::getRate);
        if (directRate.isPresent()) {
            result = directRate.get();
        }
        return Optional.ofNullable(result);
    }

    private Optional<BigDecimal> findByRateThroughUsd(int baseId, int targetId) {
        BigDecimal result = null;
        var usd = getCurrencyDao().findByCode("USD");
        if (usd.isPresent()) {
            var usdAndBase = createExchangeRateData(usd.get().getId(), baseId);
            var usdAndTarget = createExchangeRateData(usd.get().getId(), targetId);
            var rateUsdToBase = exchangeRateDao.findByCurrencyIDs(usdAndBase);
            var rateUsdToTarget = exchangeRateDao.findByCurrencyIDs(usdAndTarget);
            if (rateUsdToBase.isPresent() && rateUsdToTarget.isPresent()) {
                result = rateUsdToTarget.get().getRate()
                        .divide(rateUsdToBase.get().getRate(), 3, RoundingMode.CEILING);
            }
        }
        return Optional.ofNullable(result);
    }

    private ExchangeRateData createExchangeRateData(int baseId, int targetId) {
        return ExchangeRateData.builder()
                .baseCurrencyId(baseId)
                .targetCurrencyId(targetId)
                .build();
    }
}
