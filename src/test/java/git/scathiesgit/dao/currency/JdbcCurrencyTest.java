package git.scathiesgit.dao.currency;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcCurrencyTest {

    private static final CurrencyDao CURRENCY_DAO = new JdbcCurrency();

    @Test
    void whenFindByIdThenReturnCurrency() {
        var currencies = CURRENCY_DAO.findAll();
        var expectedCurrency = currencies.get(0);
        var actualCurrency = CURRENCY_DAO.findById(expectedCurrency.getId()).get();
        assertThat(actualCurrency).isEqualTo(expectedCurrency);
    }

    @Test
    void whenFindByCodeThenReturnCurrency() {
        var currencies = CURRENCY_DAO.findAll();
        var expectedCurrency = currencies.get(0);
        var actualCurrency = CURRENCY_DAO.findByCode(expectedCurrency.getCode()).get();
        assertThat(actualCurrency).isEqualTo(expectedCurrency);
    }
}