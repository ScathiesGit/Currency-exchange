package git.scathiesgit.dao;

import git.scathiesgit.dto.CurrencyDto;
import git.scathiesgit.entity.Currency;
import git.scathiesgit.util.ConnectionManager;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CurrencyDaoTest {

    private static CurrencyDao currencyDao = CurrencyDao.getInstance();

    @Test
    void notEmptyDB_getAllCurrencies_notEmptyList() {
        List<Currency> currencies = currencyDao.findAll();

        assertThat(currencies.isEmpty()).isFalse();
    }

    @Test
    void getCurrencyWithExistCode_notZeroId() {
        var existCurrencyCode = "USD";

        var currency = currencyDao.findByCode(existCurrencyCode);

        assertThat(currency.getId()).isPositive();
    }

    @Test
    void getCurrencyWithNonExistCode_zeroId() {
        var nonExistCurrencyCode = "R";
        var expected = 0;

        var currency = currencyDao.findByCode(nonExistCurrencyCode);
        var actual = currency.getId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void saveCurrencyWithNotExistCode_notZeroId() {
        var name = "TestCurrency";
        var code = "TestCode";
        var sign = "#$%&";

        var actual = currencyDao.save(CurrencyDto.builder()
                .name(name)
                .code(code)
                .sign(sign)
                .build());

        assertThat(actual).isNotZero();

        deleteAddedCurrency(actual);
    }

    private void deleteAddedCurrency(int id) {
        try (var connection = ConnectionManager.open(); var statement = connection.prepareStatement(
                "DELETE FROM Currencies WHERE id = ?"
        )) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveCurrencyWithExistCode_shouldThrownRuntimeException() {
        var dto = CurrencyDto.builder()
                .code("USD")
                .build();

        assertThatThrownBy(() -> currencyDao.save(dto))
                .isInstanceOf(RuntimeException.class);
    }
}
