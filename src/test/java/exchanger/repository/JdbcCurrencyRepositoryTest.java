package exchanger.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcCurrencyRepositoryTest {

    private final CurrencyRepository currencyRepo = new JdbcCurrencyRepository();

    @BeforeEach
    public void before() {

    }

    @Test
    void whenFindByIdThenReturnCurrency() {
        var currencies = currencyRepo.findAll();
        var expectedCurrency = currencies.get(0);
        var actualCurrency = currencyRepo.findById(expectedCurrency.getId()).get();
        assertThat(actualCurrency).isEqualTo(expectedCurrency);
    }

    @Test
    void whenFindAllThenReturnNotEmpty(){
        assertThat(currencyRepo.findAll()).isEqualTo(List.of());
    }

    @Test
    @Disabled
    void whenFindByCodeThenReturnCurrency() {
        var currencies = currencyRepo.findAll();
        var expectedCurrency = currencies.get(0);
        var actualCurrency = currencyRepo.findByCode(expectedCurrency.getCode()).get();
        assertThat(actualCurrency).isEqualTo(expectedCurrency);
    }
}