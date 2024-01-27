package git.scathiesgit.dao;

import git.scathiesgit.entity.ExchangeRate;
import git.scathiesgit.util.ConnectionManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExchangeRateDaoImplTest {

    private static final ExchangeRateDaoImpl EXCHANGE_RATE_DAO = new ExchangeRateDaoImpl();

    @Test
    void notEmptyDatabase_findAll_notEmptyList() {
        var exchangeRates = EXCHANGE_RATE_DAO.findAll();

        assertThat(exchangeRates.isEmpty()).isFalse();
    }

    @Test
    void tableContainsCurrencyIDs_findByCurrencyIDs_returnExchangeRate() {
        var exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(1)
                .targetCurrencyId(2)
                .build();

        var actual = EXCHANGE_RATE_DAO.findByCurrencyIDs(exchangeRate);

        assertThat(actual.isPresent()).isTrue();
    }

    @Test
    void tableNotContainsCurrencyIDs_findByCurrencyIDs_returnEmpty() {
        var exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(321)
                .targetCurrencyId(32157)
                .build();

        var actual = EXCHANGE_RATE_DAO.findByCurrencyIDs(exchangeRate);

        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void foreignKeysNotExist_save_throwRuntimeException() {
        var exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(123445)
                .targetCurrencyId(54322)
                .rate(BigDecimal.valueOf(12.2))
                .build();

        var actual = EXCHANGE_RATE_DAO.save(exchangeRate);

        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void tableContainsExchangeRate_save_throwRuntimeException() {
        var exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(1)
                .targetCurrencyId(2)
                .rate(BigDecimal.valueOf(123.3))
                .build();

        assertThatThrownBy(() -> EXCHANGE_RATE_DAO.save(exchangeRate))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void tableNotContainsExchangeRate_save_returnId() {
        var exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(3)
                .targetCurrencyId(2)
                .rate(BigDecimal.valueOf(1.111))
                .build();

        var actual = EXCHANGE_RATE_DAO.save(exchangeRate);

        assertThat(actual.isPresent()).isTrue();

        deleteAddedExchangeRate(actual.getAsInt());
    }

    @SneakyThrows
    private void deleteAddedExchangeRate(int id) {
       try (var connection = ConnectionManager.open();
            var statement = connection.prepareStatement("DELETE FROM ExchangeRates WHERE ID = ?")) {
           statement.setInt(1, id);
           statement.executeUpdate();
       }
    }

    @Test
    void notExistExchangeRate_update_returnEmpty() {
        var exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(3)
                .targetCurrencyId(1)
                .rate(BigDecimal.valueOf(1.11))
                .build();

        var actual = EXCHANGE_RATE_DAO.update(exchangeRate);

        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void existExchangeRate_update_returnId() {
        var exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(1)
                .targetCurrencyId(2)
                .rate(BigDecimal.valueOf(1.112))
                .build();

        var actual = EXCHANGE_RATE_DAO.update(exchangeRate);

        assertThat(actual.isPresent()).isTrue();
    }
}
