package git.scathiesgit.dao;

import git.scathiesgit.entity.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class CurrencyDaoImpl implements CurrencyDao {

    private final Executor executor = new Executor();

    private static final String SAVE_SQL = """
            INSERT INTO Currencies(Code, FullName, Sign)
            VALUES (?, ?, ?)
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT Id, Code, FullName, Sign
            FROM Currencies
            """;

    private static final String SELECT_BY_CODE_SQL = """
            SELECT Id, Code, FullName, Sign
            FROM Currencies
            WHERE Code = ?
            """;

    private static final String SELECT_BY_ID = """
            SELECT Id, Code, FullName, Sign
            FROM Currencies
            WHERE Id = ?
            """;

    @Override
    public Optional<Currency> findByCode(String code) {
        return executor.executeQuery(SELECT_BY_CODE_SQL, statement -> {
            try {
                statement.setString(1, code);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, resultSet -> {
            Currency currency = null;
            try {
                if (resultSet.next()) {
                    currency = toCurrency(resultSet);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(currency);
        });
    }

    @Override
    public Optional<Currency> findById(int id) {
        return executor.executeQuery(SELECT_BY_ID, statement -> {
            try {
                statement.setInt(1, id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, resultSet -> {
            Currency currency = null;
            try {
                if (resultSet.next()) {
                    currency = toCurrency(resultSet);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(currency);
        });
    }

    @Override
    public List<Currency> findAll() {
        return executor.executeQuery(SELECT_ALL_SQL, statement -> {
        }, resultSet -> {
            List<Currency> currencies = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    currencies.add(toCurrency(resultSet));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return currencies;
        });
    }

    @Override
    public OptionalInt save(Currency currency) {
        return executor.executeUpdate(SAVE_SQL, Statement.RETURN_GENERATED_KEYS, statement -> {
            try {
                statement.setString(1, currency.getCode());
                statement.setString(2, currency.getFullName());
                statement.setString(3, currency.getSign());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
}
