package exchanger.repository;

import exchanger.entity.ExchangeRate;
import exchanger.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateRepository implements ExchangeRateRepository {

    private static final String SAVE = """
            INSERT INTO ExchangeRates(baseCurrencyId, targetCurrencyId, rate)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE ExchangeRates
            SET Rate = ?
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;

    private static final String DELETE = """
            DELETE FROM ExchangeRates
            WHERE Id = ?
            """;

    private static final String SELECT_BY_CURRENCY_ID = """
            SELECT id, BaseCurrencyId, TargetCurrencyId, Rate
            FROM ExchangeRates
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;

    private static final String SELECT_ALL = """
            SELECT id, BaseCurrencyId, TargetCurrencyId, Rate
            FROM ExchangeRates;
            """;

    @Override
    public int save(ExchangeRate exchangeRate) {
        var generatedKey = -1;
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
             var enableForeignKeys = con.prepareStatement("PRAGMA foreign_keys = ON;")
        ) {
            enableForeignKeys.execute();

            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());

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
    public boolean update(ExchangeRate exchangeRate) {
        var isEffected = false;
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(UPDATE)
        ) {
            statement.setBigDecimal(1, exchangeRate.getRate());
            statement.setInt(2, exchangeRate.getBaseCurrencyId());
            statement.setInt(3, exchangeRate.getTargetCurrencyId());
            isEffected = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isEffected;
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
    public Optional<ExchangeRate> findByCurrency(int baseId, int targetId) {
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(SELECT_BY_CURRENCY_ID)
        ) {
            statement.setInt(1, baseId);
            statement.setInt(2, targetId);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var currency = toExchangeRate(resultSet);
                return Optional.of(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() {
        var rsl = new ArrayList<ExchangeRate>();
        try (var con = ConnectionManager.open();
             var statement = con.prepareStatement(SELECT_ALL)
        ) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rsl.add(
                        toExchangeRate(resultSet)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rsl;
    }

    private ExchangeRate toExchangeRate(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getInt("Id"))
                .baseCurrencyId(resultSet.getInt("BaseCurrencyId"))
                .targetCurrencyId(resultSet.getInt("TargetCurrencyId"))
                .rate(resultSet.getBigDecimal("Rate"))
                .build();
    }

    private JdbcExchangeRateRepository() {
    }

    private static class SingletonHolder {
        private static final JdbcExchangeRateRepository INSTANCE = new JdbcExchangeRateRepository();
    }

    public static JdbcExchangeRateRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
