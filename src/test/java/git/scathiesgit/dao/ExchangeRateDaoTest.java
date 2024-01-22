package git.scathiesgit.dao;

import git.scathiesgit.dto.ExchangeRateDto;
import git.scathiesgit.util.ConnectionManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExchangeRateDaoTest {

    private static ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    @Test
    void notEmptyDB_getAllExchangeRate_notEmptyList() {
        var exchangeRates = exchangeRateDao.getAllExchangeRate();

        assertThat(exchangeRates.isEmpty()).isFalse();
    }

    @Test
    void tableContainsCurrencyCodes_findExchangeRateByCodes_notZeroId() {
        var baseCurrencyCode = "USD";
        var targetCurrencyCode = "EUR";

        var actual = exchangeRateDao.findByCurrenciesCodes(baseCurrencyCode, targetCurrencyCode).getId();

        assertThat(actual).isPositive();
    }

    @Test
    void tableNotContainsCurrencyCodes_findExchangeRateByCodes_zeroId() {
        var baseCurrencyCode = "U";
        var targetCurrencyCode = "R";

        var actual = exchangeRateDao.findByCurrenciesCodes(baseCurrencyCode, targetCurrencyCode).getId();

        assertThat(actual).isZero();
    }

    @Test
    void notExistCurrencyCode_saveExchangeRate_shouldThrowRuntimeException() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("EUR")
                .targetCurrencyCode("E")
                .rate(BigDecimal.valueOf(12.2))
                .build();

        assertThatThrownBy(() -> exchangeRateDao.save(dto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void existExchangeRate_saveExchangeRate_shouldThrowRuntimeException() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("USD")
                .targetCurrencyCode("EUR")
                .rate(BigDecimal.valueOf(1.2))
                .build();

        assertThatThrownBy(() -> exchangeRateDao.save(dto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void dbNotContainsExchangeRate_save_idIsNotZero() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("GBP")
                .targetCurrencyCode("JPY")
                .rate(BigDecimal.valueOf(170.5))
                .build();

        var actual = exchangeRateDao.save(dto);

        assertThat(actual).isPositive();
        deleteAddedExchangeRate(actual);
    }

    private void deleteAddedExchangeRate(int id) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(
                     "DELETE FROM ExchangeRates WHERE ID = ?"
             )) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void notExistExchangeRate_update_shouldThrowRuntimeException() {
        var exchangeRate = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("GBP")
                .targetCurrencyCode("EUR")
                .rate(BigDecimal.valueOf(1.11))
                .build();

        assertThatThrownBy(() -> exchangeRateDao.update(exchangeRate))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void existExchangeRate_update_idIsNotZero() {
        var exchangeRate = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("USD")
                .targetCurrencyCode("EUR")
                .rate(BigDecimal.valueOf(1.15))
                .build();

        var actual = exchangeRateDao.update(exchangeRate);

        assertThat(actual).isPositive();
    }

    @Test
    void notExistCurrencyCodes_update_shouldThrowRuntimeException() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("USD")
                .targetCurrencyCode("Y")
                .rate(BigDecimal.valueOf(1.22))
                .build();

        assertThatThrownBy(() -> exchangeRateDao.update(dto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void existStraightRate_findExchangeRate_returnRateIsNotZero() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("USD")
                .targetCurrencyCode("EUR")
                .build();

        var rate = exchangeRateDao.findExchangeRate(dto);

        assertThat(rate).isPositive();
    }

    @Test
    void existReverseRate_findExchangeRate_returnRateIsNotZero() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("EUR")
                .targetCurrencyCode("USD")
                .build();

        var rate = exchangeRateDao.findExchangeRate(dto);

        assertThat(rate).isPositive();
    }

    @Test
    void existCrossRate_findExchangeRate_returnRateIsNotZero() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("EUR")
                .targetCurrencyCode("GBP")
                .build();

        var rate = exchangeRateDao.findExchangeRate(dto);

        assertThat(rate).isPositive();
    }

    @Test
    void whenImpossibleFindRate_findExchangeRate_shouldThrowRuntimeException() {
        var dto = ExchangeRateDto.newBuilder()
                .baseCurrencyCode("JPY")
                .targetCurrencyCode("AUD")
                .build();

        assertThatThrownBy(() -> exchangeRateDao.update(dto))
                .isInstanceOf(RuntimeException.class);
    }
}
