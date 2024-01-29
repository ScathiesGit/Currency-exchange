package git.scathiesgit.dao.exchangerate;

import git.scathiesgit.dto.ExchangeRateData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcExchangeRateTest {

    private static final ExchangeRateDao EXCHANGE_RATE_DAO = new JdbcExchangeRate();

    @Test
    void whenFindByCurrencyIDsThenReturnExistExchangeRate() {
        var exchangeRates = EXCHANGE_RATE_DAO.findAll();
        var expectedExchangeRate = exchangeRates.get(0);
        var actualExchangeRate = EXCHANGE_RATE_DAO.findByCurrencyIDs(
                ExchangeRateData.builder()
                        .baseCurrencyId(expectedExchangeRate.getBaseCurrencyId())
                        .targetCurrencyId(expectedExchangeRate.getTargetCurrencyId())
                        .build()
        ).get();
        assertThat(actualExchangeRate).isEqualTo(expectedExchangeRate);
    }

    @Test
    void whenUpdateExchangeRateThenFindByCurrencyIDsUpdated() {
        var exchangeRates = EXCHANGE_RATE_DAO.findAll();
        var oldRate = exchangeRates.get(0);
        var toUpdate = new ExchangeRateData(oldRate.getBaseCurrencyId(), oldRate.getTargetCurrencyId(),
                oldRate.getRate().add(BigDecimal.ONE));
        EXCHANGE_RATE_DAO.update(toUpdate).getAsInt();
        var actualRate = EXCHANGE_RATE_DAO.findByCurrencyIDs(toUpdate).get();
        assertThat(actualRate.getRate()).isNotEqualTo(oldRate.getRate());
        assertThat(actualRate)
                .extracting("id", "baseCurrencyId", "targetCurrencyId")
                .containsExactly(oldRate.getId(), oldRate.getBaseCurrencyId(), oldRate.getTargetCurrencyId());
    }

}