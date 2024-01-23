package git.scathiesgit.dao;

import git.scathiesgit.dto.CurrencyDto;
import git.scathiesgit.entity.Currency;
import git.scathiesgit.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    public static final int ID_COL = 1;
    public static final int CODE_COL = 2;
    public static final int FULL_NAME_COL = 3;
    public static final int SIGN_COL = 4;

    private static final String INSERT_SQL = """
            INSERT INTO Currencies(Code, FullName, Sign)
            VALUES (?, ?, ?)
            """;

    private static final String GET_ALL_CURRENCIES_SQL = """
            SELECT *
            FROM Currencies
            """;

    public static final String GET_CURRENCY_BY_CODE_SQL = """
            SELECT *
            FROM Currencies
            WHERE Code = ?
            """;

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    public Currency findByCode(String code) {
        var resultCurrency = new Currency();
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(GET_CURRENCY_BY_CODE_SQL)) {
            statement.setString(1, code);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                resultCurrency = toCurrency(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultCurrency;
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(GET_ALL_CURRENCIES_SQL)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var currency = toCurrency(resultSet);
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    public int save(CurrencyDto currency) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
            return statement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency toCurrency(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getInt(ID_COL))
                .code(resultSet.getString(CODE_COL))
                .fullName(resultSet.getString(FULL_NAME_COL))
                .sign(resultSet.getString(SIGN_COL))
                .build();
    }
}
