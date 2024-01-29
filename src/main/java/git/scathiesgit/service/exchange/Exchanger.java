package git.scathiesgit.service.exchange;

import git.scathiesgit.dto.ExchangeData;
import git.scathiesgit.service.CurrencyExchange;
import git.scathiesgit.service.search.RateSearcher;
import git.scathiesgit.service.search.ExchangeRateSearcher;

import java.math.BigDecimal;
import java.util.Optional;

public class Exchanger implements CurrencyExchange {

    private static final ExchangeRateSearcher searcher = new RateSearcher();

    @Override
    public Optional<BigDecimal> exchange(ExchangeData data) {
        BigDecimal convertedAmount = null;
        var rate = searcher.findRate(data.getBaseCurrencyCode(), data.getTargetCurrencyCode());
        if (rate.isPresent()) {
            convertedAmount = rate.get().multiply(BigDecimal.valueOf(data.getAmount()));
        }
        return Optional.ofNullable(convertedAmount);
    }
}
