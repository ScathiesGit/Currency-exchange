package exchanger.repository;

import exchanger.entity.Currency;
import exchanger.entity.ExchangeRate;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class JdbcExchangeRateRepositoryTest {

    private final ExchangeRateRepository exchangeRateRepo = JdbcExchangeRateRepository.getInstance();

    private final CurrencyRepository currencyRepo = JdbcCurrencyRepository.getInstance();

    private final Currency usd = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();

    private final Currency eur = Currency.builder().id(2).code("EUR").fullName("Euro").sign("#").build();

    private final ExchangeRate usdToEur = ExchangeRate.builder()
            .baseCurrencyId(usd.getId())
            .targetCurrencyId(eur.getId())
            .rate(BigDecimal.valueOf(1.23))
            .build();

    @BeforeEach
    void setUp() {
        currencyRepo.findAll()
                .forEach(
                        currency -> currencyRepo.delete(currency.getId())
                );

        exchangeRateRepo.findAll()
                .forEach(
                        exchangeRate -> exchangeRateRepo.delete(exchangeRate.getId())
                );
    }

    @Test
    void givenExistCurrencyWhenSaveThenFindByCurrency() {
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        usdToEur.setBaseCurrencyId(usd.getId());
        usdToEur.setTargetCurrencyId(eur.getId());

        usdToEur.setId(exchangeRateRepo.save(usdToEur));
        var found = exchangeRateRepo.findByCurrency(usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()).get();

        assertThat(found).isEqualTo(usdToEur);
    }

    @Test
    void givenNotExistCurrencyWhenSaveThenThrowRuntimeException() {
        assertThatThrownBy(() -> exchangeRateRepo.save(usdToEur))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenExistRateWhenUpdateThenFindUpdatedRate() {
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        usdToEur.setBaseCurrencyId(usd.getId());
        usdToEur.setTargetCurrencyId(eur.getId());
        usdToEur.setId(exchangeRateRepo.save(usdToEur));
        usdToEur.setRate(BigDecimal.valueOf(1.2345));

        exchangeRateRepo.update(usdToEur);

        var updatedRate = exchangeRateRepo.findByCurrency(usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId())
                .get();
        assertThat(usdToEur)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder().build())
                .isEqualTo(updatedRate);
    }

    @Test
    void givenNotExistRateWhenUpdateThenNotFindByCurrency() {
        var isUpdated = exchangeRateRepo.update(usdToEur);

        var updatedRate = exchangeRateRepo.findByCurrency(usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId());

        assertAll(
                () -> assertThat(updatedRate).isEmpty(),
                () -> assertThat(isUpdated).isFalse()
        );
    }

    @Test
    void whenDeleteThenNotFindByCurrency() {
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        usdToEur.setBaseCurrencyId(usd.getId());
        usdToEur.setTargetCurrencyId(eur.getId());
        usdToEur.setId(exchangeRateRepo.save(usdToEur));

        exchangeRateRepo.delete(usdToEur.getId());

        var found = exchangeRateRepo.findByCurrency(
                usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()
        );
        assertThat(found).isEmpty();
    }

    @Test
    void givenExistRateWhenFindByCurrencyThenReturnExchangeCurrency() {
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        usdToEur.setBaseCurrencyId(usd.getId());
        usdToEur.setTargetCurrencyId(eur.getId());
        usdToEur.setId(exchangeRateRepo.save(usdToEur));

        var exchangeRate = exchangeRateRepo.findByCurrency(
                usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()
        ).get();

        assertThat(exchangeRate)
                .usingRecursiveComparison()
                .isEqualTo(usdToEur);
    }

    @Test
    void givenNotExistRateWhenFindByCurrencyTHenReturnEmpty() {
        var exchangeRate = exchangeRateRepo.findByCurrency(
                usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()
        );

        assertThat(exchangeRate).isEmpty();
    }

    @Test
    void givenNotEmptyDbWhenFindAllThenReturnAllExchangeRates() {
        var rub = Currency.builder().code("RUB").fullName("Russian Ruble").sign("₽").build();
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        rub.setId(currencyRepo.save(rub));
        usdToEur.setBaseCurrencyId(usd.getId());
        usdToEur.setTargetCurrencyId(eur.getId());
        var usdToRub = ExchangeRate.builder()
                .baseCurrencyId(usd.getId())
                .targetCurrencyId(rub.getId())
                .rate(BigDecimal.valueOf(80))
                .build();
        usdToEur.setId(exchangeRateRepo.save(usdToEur));
        usdToRub.setId(exchangeRateRepo.save(usdToRub));

        var exchangeRates = exchangeRateRepo.findAll();

        assertThat(exchangeRates).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(usdToRub, usdToEur));
    }

    @Test
    void givenEmptyDbWhenFindAllThenReturnEmptyList() {
        var exchangeRates = exchangeRateRepo.findAll();

        assertThat(exchangeRates).isEmpty();
    }
}