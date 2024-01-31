package exchanger.repository;

import exchanger.entity.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcCurrencyRepositoryTest {

    private final CurrencyRepository currencyRepo = new JdbcCurrencyRepository();

    @BeforeEach
    void before() {
        currencyRepo.findAll()
                .forEach(
                        currency -> currencyRepo.delete(currency.getId())
                );
    }

    @Test
    void whenSaveCurrencyThenFindById() {
        var toSave = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();

        toSave.setId(currencyRepo.save(toSave));

        var found = currencyRepo.findById(toSave.getId()).get();
        assertThat(toSave).isEqualTo(found);
    }

    @Test
    void whenDeleteCurrencyThenNotFindById() {
        var toDelete = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        toDelete.setId(currencyRepo.save(toDelete));

        currencyRepo.delete(toDelete.getId());

        assertThat(
                currencyRepo.findById(toDelete.getId())
        ).isEmpty();
    }

    @Test
    void givenNotExistIdWhenDeleteThenReturnFalse() {
        var toDelete = new Currency(1, "USD", "US Dollar", "$");

        var isDeleted = currencyRepo.delete(toDelete.getId());

        assertThat(isDeleted).isFalse();
    }

    @Test
    void givenExistIdWhenFindByIdThenReturnCurrency() {
        var currency = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        currency.setId(currencyRepo.save(currency));

        var foundCurrency = currencyRepo.findById(currency.getId()).get();

        assertThat(foundCurrency)
                .usingRecursiveComparison()
                .isEqualTo(currency);
    }

    @Test
    void givenNotExistIdWhenFindByIdThenReturnEmpty() {
        var foundCurrency = currencyRepo.findById(1);

        assertThat(foundCurrency).isEmpty();
    }

    @Test
    void givenExistCodeWhenFindByCodeThenReturnCurrency() {
        var currency = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        currency.setId(currencyRepo.save(currency));

        var foundCurrency = currencyRepo.findByCode(currency.getCode()).get();

        assertThat(foundCurrency)
                .usingRecursiveComparison()
                .isEqualTo(currency);
    }

    @Test
    void givenNotExistCodeWhenFindByCodeThenReturnEmpty() {
        var foundCurrency = currencyRepo.findByCode("USD");

        assertThat(foundCurrency).isEmpty();
    }

    @Test
    void givenEmptyDbWhenFindAllThenEmptyList() {
        var currencies = currencyRepo.findAll();
        assertThat(currencies).isEmpty();
    }

    @Test
    void givenNotEmptyDbWhenFindAllThenNotEmptyList() {
        var usd = Currency.builder().code("USD").fullName("US Dollar").sign("$").build();
        var eur = Currency.builder().code("EUR").fullName("EUR").sign("€").build();
        var rub = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();
        usd.setId(currencyRepo.save(usd));
        eur.setId(currencyRepo.save(eur));
        rub.setId(currencyRepo.save(rub));

        var currencies = currencyRepo.findAll();

        assertThat(currencies).hasSize(3)
                .containsExactlyInAnyOrderElementsOf(List.of(usd, eur, rub));
    }

}