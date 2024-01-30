package exchanger.repository;

import exchanger.entity.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcExchangeRateRepositoryTest {

    private final ExchangeRateRepository exchangeRateRepo = new JdbcExchangeRateRepository();

    @Test
    void whenFindByCurrencyIDsThenReturnExistExchangeRate() {
        var exchangeRates = exchangeRateRepo.findAll();
        var expectedExchangeRate = exchangeRates.get(0);
        var actualExchangeRate = exchangeRateRepo.findByCurrency(
                expectedExchangeRate.getBaseCurrencyId(),
                expectedExchangeRate.getTargetCurrencyId()
        ).get();
        assertThat(actualExchangeRate).isEqualTo(expectedExchangeRate);
    }

    @Test
    void whenUpdateExchangeRateThenFindByCurrencyIDsUpdated() {
        var exchangeRates = exchangeRateRepo.findAll();
        var oldRate = exchangeRates.get(0);
        var toUpdate = new ExchangeRate(
                oldRate.getId(),
                oldRate.getBaseCurrencyId(),
                oldRate.getTargetCurrencyId(),
                oldRate.getRate().add(BigDecimal.ONE)
        );
        assertThat(exchangeRateRepo.update(toUpdate)).isTrue();
        var actualRate = exchangeRateRepo.findByCurrency(
                toUpdate.getBaseCurrencyId(),
                toUpdate.getTargetCurrencyId()
        ).get();
        assertThat(actualRate.getRate()).isNotEqualTo(oldRate.getRate());
        assertThat(actualRate)
                .extracting("id", "baseCurrencyId", "targetCurrencyId")
                .containsExactly(oldRate.getId(), oldRate.getBaseCurrencyId(), oldRate.getTargetCurrencyId());
    }

}