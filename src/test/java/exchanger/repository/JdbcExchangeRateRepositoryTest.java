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

class JdbcExchangeRateRepositoryTest {

    private final ExchangeRateRepository exchangeRateRepo = new JdbcExchangeRateRepository();

    private final CurrencyRepository currencyRepo = new JdbcCurrencyRepository();

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
        var usd = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        var eur = Currency.builder().code("EUR").fullName("Euro").sign("#").build();
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(usd.getId())
                .targetCurrencyId(eur.getId())
                .rate(BigDecimal.ONE)
                .build();

        usdToEur.setId(exchangeRateRepo.save(usdToEur));
        var found = exchangeRateRepo.findByCurrency(usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()).get();

        assertThat(found).isEqualTo(usdToEur);
    }

    @Test
    void givenNotExistCurrencyWhenSaveThenThrowRuntimeException() {
        var usd = new Currency(1, "USD", "US Dollar", "$");
        var eur = new Currency(2, "EUR", "Euro", "&");
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(usd.getId())
                .targetCurrencyId(eur.getId())
                .rate(BigDecimal.ONE)
                .build();

        assertThatThrownBy(() -> exchangeRateRepo.save(usdToEur))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenExistRateWhenUpdateThenFindUpdatedRate() {
        var usd = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        var eur = Currency.builder().code("EUR").fullName("Euro").sign("#").build();
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(usd.getId())
                .targetCurrencyId(eur.getId())
                .rate(BigDecimal.ONE)
                .build();
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
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(1)
                .targetCurrencyId(2)
                .rate(BigDecimal.ONE)
                .build();

        var isUpdated = exchangeRateRepo.update(usdToEur);

        var updatedRate = exchangeRateRepo.findByCurrency(usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId());
        assertThat(updatedRate).isEmpty();
        assertThat(isUpdated).isFalse();
    }

    @Test
    void whenDeleteThenNotFindByCurrency() {
        var usd = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        var eur = Currency.builder().code("EUR").fullName("Euro").sign("#").build();
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(usd.getId())
                .targetCurrencyId(eur.getId())
                .rate(BigDecimal.ONE)
                .build();
        usdToEur.setId(exchangeRateRepo.save(usdToEur));

        exchangeRateRepo.delete(usdToEur.getId());

        var found = exchangeRateRepo.findByCurrency(
                usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()
                );
        assertThat(found).isEmpty();
    }

    @Test
    void givenExistRateWhenFindByCurrencyThenReturnExchangeCurrency() {
        var usd = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        var eur = Currency.builder().code("EUR").fullName("Euro").sign("#").build();
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(usd.getId())
                .targetCurrencyId(eur.getId())
                .rate(BigDecimal.valueOf(1.2))
                .build();
        usdToEur.setId(exchangeRateRepo.save(usdToEur));

        var exchangeRate = exchangeRateRepo.findByCurrency(
                usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()
        ).get();

        assertThat(exchangeRate)
                .usingRecursiveComparison()
                .isEqualTo(usdToEur);
    }

    @Test
    void givenNotExistRateWHenFindByCurrencyTHenReturnEmpty() {
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(1)
                .targetCurrencyId(2)
                .rate(BigDecimal.valueOf(1.2))
                .build();

        var exchangeRate = exchangeRateRepo.findByCurrency(
                usdToEur.getBaseCurrencyId(), usdToEur.getTargetCurrencyId()
        );

        assertThat(exchangeRate).isEmpty();
    }

    @Test
    void givenNotEmptyDbWhenFindAllThenReturnAllExchangeRates() {
        var usd = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        var eur = Currency.builder().code("EUR").fullName("Euro").sign("#").build();
        var rub = Currency.builder().code("RUB").fullName("Russian Ruble").sign("₽").build();
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        rub.setId(currencyRepo.save(rub));
        var usdToEur = ExchangeRate.builder()
                .baseCurrencyId(usd.getId())
                .targetCurrencyId(eur.getId())
                .rate(BigDecimal.valueOf(1.2))
                .build();
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