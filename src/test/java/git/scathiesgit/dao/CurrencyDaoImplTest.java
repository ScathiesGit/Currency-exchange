package git.scathiesgit.dao;

import git.scathiesgit.entity.Currency;
import git.scathiesgit.util.ConnectionManager;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CurrencyDaoImplTest {

    private static final CurrencyDao CURRENCY_DAO = new CurrencyDaoImpl();

    @Test
    void notEmptyDatabase_findAll_notEmptyList() {
        List<Currency> currencies = CURRENCY_DAO.findAll();

        assertThat(currencies.isEmpty()).isFalse();
    }

    @Test
    void idExist_findById_returnCurrency() {
        var id = 1;

        var currency = CURRENCY_DAO.findById(id);

        assertThat(currency.isPresent()).isTrue();
    }

    @Test
    void idNotExist_findById_returnEmpty() {
        var id = 999999;

        var currency = CURRENCY_DAO.findById(id);

        assertThat(currency.isEmpty()).isTrue();
    }

    @Test
    void codeExistInDatabase_findByCode_returnCurrency() {
        var existCurrencyCode = "USD";

        var currency = CURRENCY_DAO.findByCode(existCurrencyCode);

        assertThat(currency.isPresent()).isTrue();
    }

    @Test
    void codeNotExistInDatabase_findByCode_returnEmpty() {
        var notExistCurrencyCode = "R";
        var expected = Optional.empty();

        var actual = CURRENCY_DAO.findByCode(notExistCurrencyCode);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void codeExistInDatabase_save_throwRuntimeException() {
        var currency = Currency.builder()
                .code("USD")
                .build();

        assertThatThrownBy(() -> CURRENCY_DAO.save(currency))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void codeNotExistInDatabase_save_returnCurrency() {
        var currency = Currency.builder()
                .code("test_code")
                .fullName("test_name")
                .sign("test_sign")
                .build();

        var actual = CURRENCY_DAO.save(currency);

        assertThat(actual.isPresent()).isTrue();

        deleteAddedCurrency(currency);
    }

    private void deleteAddedCurrency(Currency currency) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(
                     "DELETE FROM Currencies WHERE code = ?"
             )) {
            statement.setString(1, currency.getCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
