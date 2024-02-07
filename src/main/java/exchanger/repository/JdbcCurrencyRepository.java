package exchanger.repository;

import exchanger.entity.Currency;
import exchanger.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyRepository implements CurrencyRepository {

    private static final String SAVE = """
            INSERT INTO Currencies(Code, FullName, Sign)
            VALUES (?, ?, ?)
            """;

    private static final String DELETE = """
            DELETE FROM Currencies
            WHERE Id = ?
            """;

    private static final String SELECT_BY_ID = """
            SELECT Id, Code, FullName, Sign
            FROM Currencies
            WHERE Id = ?
            """;

    private static final String SELECT_BY_CODE = """
            SELECT Id, Code, FullName, Sign
            FROM Currencies
            WHERE Code = ?
            """;

    private static final String SELECT_ALL = """
            SELECT Id, Code, FullName, Sign
            FROM Currencies
            """;

    @Override
    public int save(Currency currency) {
        var generatedKey = -1;
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());

            statement.executeUpdate();

            var resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                generatedKey = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return generatedKey;
    }

    @Override
    public boolean delete(int id) {
        var isDeleted = false;
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(DELETE)
        ) {
            statement.setInt(1, id);
            isDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isDeleted;
    }

    @Override
    public Optional<Currency> findById(int id) {
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(SELECT_BY_ID)
        ) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var currency = toCurrency(resultSet);
                return Optional.of(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(SELECT_BY_CODE)
        ) {
            statement.setString(1, code);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var currency = toCurrency(resultSet);
                return Optional.of(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() {
        List<Currency> rsl = new ArrayList<>();
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(SELECT_ALL)
        ) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rsl.add(
                        toCurrency(resultSet)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rsl;
    }

    private Currency toCurrency(ResultSet resultSet) {
        try {
            return Currency.builder()
                    .id(resultSet.getInt("Id"))
                    .code(resultSet.getString("Code"))
                    .fullName(resultSet.getString("FullName"))
                    .sign(resultSet.getString("Sign"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private JdbcCurrencyRepository() {
    }

    private static class SingletonHolder {
        public static final JdbcCurrencyRepository INSTANCE = new JdbcCurrencyRepository();
    }

    public static JdbcCurrencyRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
