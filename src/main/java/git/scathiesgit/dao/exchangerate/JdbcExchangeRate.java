package git.scathiesgit.dao.exchangerate;

import git.scathiesgit.dao.JdbcExecutor;
import git.scathiesgit.dao.currency.CurrencyDao;
import git.scathiesgit.dao.currency.JdbcCurrency;
import git.scathiesgit.dto.ExchangeRateData;
import git.scathiesgit.entity.ExchangeRate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class JdbcExchangeRate implements ExchangeRateDao {

    private final JdbcExecutor jdbcExecutor = new JdbcExecutor();

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
        return jdbcExecutor.executeQuery(SELECT_ALL_SQL, statement -> {
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

    public Optional<ExchangeRate> findByCurrencyIDs(ExchangeRateData data) {
        return jdbcExecutor.executeQuery(SELECT_BY_CURRENCY_ID_SQL, statement -> {
            try {
                statement.setInt(1, data.getBaseCurrencyId());
                statement.setInt(2, data.getTargetCurrencyId());
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
    public OptionalInt save(ExchangeRateData data) {
        var result = OptionalInt.empty();
        if (isExistCurrency(data.getBaseCurrencyId()) && isExistCurrency(data.getTargetCurrencyId())) {
            result = jdbcExecutor.executeUpdate(SAVE_SQL, Statement.RETURN_GENERATED_KEYS, statement -> {
                try {
                    statement.setInt(1, data.getBaseCurrencyId());
                    statement.setInt(2, data.getTargetCurrencyId());
                    statement.setBigDecimal(3, data.getRate());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return result;
    }

    private boolean isExistCurrency(int baseId) {
        CurrencyDao currencyDao = new JdbcCurrency();
        return currencyDao.findById(baseId).isPresent();
    }

    @Override
    public OptionalInt update(ExchangeRateData data) {
        var result = OptionalInt.empty();
        var exchangeRate = findByCurrencyIDs(data);

        if (exchangeRate.isPresent()) {
            jdbcExecutor.executeUpdate(UPDATE_SQL, Statement.NO_GENERATED_KEYS, statement -> {
                try {
                    statement.setBigDecimal(1, data.getRate());
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
