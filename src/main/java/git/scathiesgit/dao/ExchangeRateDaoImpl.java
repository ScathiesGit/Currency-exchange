package git.scathiesgit.dao;

import git.scathiesgit.entity.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ExchangeRateDaoImpl implements ExchangeRateDao {

    private final Executor executor = new Executor();

    private static final String SELECT_ALL_SQL = """
            SELECT id, BaseCurrencyId, TargetCurrencyId, Rate
            FROM ExchangeRates;
            """;

    private static final String SELECT_BY_CURRENCY_ID_SQL = """
            SELECT id, BaseCurrencyId, TargetCurrencyId, Rate
            FROM ExchangeRates
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO ExchangeRates(baseCurrencyId, targetCurrencyId, rate)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE ExchangeRates
            SET Rate = ?
            WHERE id = ?
            """;

    @Override
    public List<ExchangeRate> findAll() {
        return executor.executeQuery(SELECT_ALL_SQL, statement -> {
        }, resultSet -> {
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    exchangeRates.add(toExchangeRate(resultSet));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return exchangeRates;
        });
    }

    public Optional<ExchangeRate> findByCurrencyIDs(ExchangeRate rate) {
        return executor.executeQuery(SELECT_BY_CURRENCY_ID_SQL, statement -> {
            try {
                statement.setInt(1, rate.getBaseCurrencyId());
                statement.setInt(2, rate.getTargetCurrencyId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, resultSet -> {
            ExchangeRate result = null;
            try {
                if (resultSet.next()) {
                    result = toExchangeRate(resultSet);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(result);
        });
    }

    @Override
    public OptionalInt save(ExchangeRate rate) {
        var result = OptionalInt.empty();
        if (isExistCurrency(rate.getBaseCurrencyId()) && isExistCurrency(rate.getTargetCurrencyId())) {
            result = executor.executeUpdate(SAVE_SQL, Statement.RETURN_GENERATED_KEYS, statement -> {
                try {
                    statement.setInt(1, rate.getBaseCurrencyId());
                    statement.setInt(2, rate.getTargetCurrencyId());
                    statement.setBigDecimal(3, rate.getRate());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return result;
    }

    private boolean isExistCurrency(int baseId) {
        CurrencyDao currencyDao = new CurrencyDaoImpl();
        return currencyDao.findById(baseId).isPresent();
    }

    @Override
    public OptionalInt update(ExchangeRate rate) {
        var result = OptionalInt.empty();
        var exchangeRate = findByCurrencyIDs(rate);

        if (exchangeRate.isPresent()) {
            executor.executeUpdate(UPDATE_SQL, Statement.NO_GENERATED_KEYS, statement -> {
                try {
                    statement.setBigDecimal(1, rate.getRate());
                    statement.setInt(2, exchangeRate.get().getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            result = OptionalInt.of(exchangeRate.get().getId());
        }
        return result;
    }

    private ExchangeRate toExchangeRate(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getInt("Id"))
                .baseCurrencyId(resultSet.getInt("BaseCurrencyId"))
                .targetCurrencyId(resultSet.getInt("TargetCurrencyId"))
                .rate(resultSet.getBigDecimal("Rate"))
                .build();
    }
}
