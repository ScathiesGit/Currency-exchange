package exchanger.repository;

import exchanger.entity.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcCurrencyRepositoryTest {

    private final CurrencyRepository currencyRepo = JdbcCurrencyRepository.getInstance();

    private final Currency usd = Currency.builder()
            .code("USD")
            .fullName("US Dollar")
            .sign("$")
            .build();

    @BeforeEach
    void before() {
        currencyRepo.findAll()
                .forEach(
                        currency -> currencyRepo.delete(currency.getId())
                );
    }

    @Test
    void whenSaveCurrencyThenFindById() {
        usd.setId(currencyRepo.save(usd));

        var found = currencyRepo.findById(usd.getId()).get();
        assertThat(usd).isEqualTo(found);
    }

    @Test
    void whenDeleteCurrencyThenNotFindById() {
        usd.setId(currencyRepo.save(usd));

        currencyRepo.delete(usd.getId());

        assertThat(
                currencyRepo.findById(usd.getId())
        ).isEmpty();
    }

    @Test
    void givenNotExistIdWhenDeleteThenReturnFalse() {
        usd.setId(1);

        var isDeleted = currencyRepo.delete(usd.getId());

        assertThat(isDeleted).isFalse();
    }

    @Test
    void givenExistIdWhenFindByIdThenReturnCurrency() {
        usd.setId(currencyRepo.save(usd));

        var foundCurrency = currencyRepo.findById(usd.getId()).get();

        assertThat(foundCurrency)
                .usingRecursiveComparison()
                .isEqualTo(usd);
    }

    @Test
    void givenNotExistIdWhenFindByIdThenReturnEmpty() {
        var foundCurrency = currencyRepo.findById(1);

        assertThat(foundCurrency).isEmpty();
    }

    @Test
    void givenExistCodeWhenFindByCodeThenReturnCurrency() {
        usd.setId(currencyRepo.save(usd));

        var foundCurrency = currencyRepo.findByCode(usd.getCode()).get();

        assertThat(foundCurrency)
                .usingRecursiveComparison()
                .isEqualTo(usd);
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