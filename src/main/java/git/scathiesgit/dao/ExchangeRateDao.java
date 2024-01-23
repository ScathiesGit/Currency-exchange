package git.scathiesgit.dao;

import git.scathiesgit.dto.ExchangeRateDto;
import git.scathiesgit.entity.ExchangeRate;
import git.scathiesgit.util.ConnectionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();

    private ExchangeRateDao() {
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    private static final int ID_COL = 1;
    private static final int BASE_CURRENCY_ID_COL = 2;
    private static final int TARGET_CURRENCY_ID_COL = 3;
    private static final int RATE_COL = 4;
    private static final int NUMBER_OF_DIGIT_AFTER_DOT = 3;

    private static final String GET_ALL_EXCHANGE_RATES_SQL = """
            SELECT *
            FROM ExchangeRates;
            """;

    private static final String GET_EXCHANGE_RATE_SQL = """
            SELECT *
            FROM ExchangeRates
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;

    private static final String INSERT_EXCHANGE_RATE_SQL = """
            INSERT INTO ExchangeRates(basecurrencyid, targetcurrencyid, rate)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_EXCHANGE_RATE_SQL = """
            UPDATE ExchangeRates
            SET Rate = ?
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;

    public int update(ExchangeRateDto dto) {
        int baseId = findCurrencyIdByCode(dto.getBaseCurrencyCode());
        int targetId = findCurrencyIdByCode(dto.getTargetCurrencyCode());
        if (baseId != 0 && targetId != 0) {
            var exchangeRateId = findByCurrenciesId(baseId, targetId).getId();
            if (exchangeRateId != 0) {
                update(baseId, targetId, dto.getRate());
                return exchangeRateId;
            }
        }
        throw new RuntimeException();
    }

    private void update(int baseId, int targetId, BigDecimal rate) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_EXCHANGE_RATE_SQL)) {
            statement.setBigDecimal(1, rate);
            statement.setInt(2, baseId);
            statement.setInt(3, targetId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int save(ExchangeRateDto dto) {
        int baseId = findCurrencyIdByCode(dto.getBaseCurrencyCode());
        int targetId = findCurrencyIdByCode(dto.getTargetCurrencyCode());
        if (baseId != 0 && targetId != 0) {
            return save(baseId, targetId, dto.getRate());
        }
        throw new RuntimeException();
    }

    private int save(int baseId, int targetId, BigDecimal rate) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(INSERT_EXCHANGE_RATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, baseId);
            statement.setInt(2, targetId);
            statement.setBigDecimal(3, rate);
            statement.executeUpdate();
            var resultSet = statement.getGeneratedKeys();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExchangeRate> getAllExchangeRate() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(GET_ALL_EXCHANGE_RATES_SQL)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(createExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    private ExchangeRate createExchangeRate(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getInt(ID_COL))
                .baseCurrencyId(resultSet.getInt(BASE_CURRENCY_ID_COL))
                .targetCurrencyId(resultSet.getInt(TARGET_CURRENCY_ID_COL))
                .rate(resultSet.getBigDecimal(RATE_COL))
                .build();
    }

    public ExchangeRate findByCurrenciesCodes(String baseCode, String targetCode) {
        int baseId = findCurrencyIdByCode(baseCode);
        int targetId = findCurrencyIdByCode(targetCode);
        return findByCurrenciesId(baseId, targetId);
    }

    private ExchangeRate findByCurrenciesId(int baseId, int targetId) {
        var resultExchangeRate = new ExchangeRate();
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(GET_EXCHANGE_RATE_SQL)) {
            statement.setInt(1, baseId);
            statement.setInt(2, targetId);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                resultExchangeRate = createExchangeRate(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultExchangeRate;
    }

    private int findCurrencyIdByCode(String code) {
        var currencyDao = CurrencyDao.getInstance();
        return currencyDao.getCurrencyByCode(code).getId();
    }

    public BigDecimal findExchangeRate(ExchangeRateDto dto) {
        var straightExchangeRate = findByCurrenciesCodes(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
        if (straightExchangeRate.getId() != 0) {
            return straightExchangeRate.getRate();
        }

        var reverseExchangeRate = findByCurrenciesCodes(dto.getTargetCurrencyCode(), dto.getBaseCurrencyCode());
        if (reverseExchangeRate.getId() != 0) {
            reverseExchangeRate.setRate(BigDecimal.ONE.divide(
                            reverseExchangeRate.getRate(), NUMBER_OF_DIGIT_AFTER_DOT, RoundingMode.CEILING));
            return reverseExchangeRate.getRate();
        }

        return findCrossRateThroughUSD(dto);
    }

    private BigDecimal findCrossRateThroughUSD(ExchangeRateDto dto) {
        var usdId = findCurrencyIdByCode("USD");
        var baseId = findCurrencyIdByCode(dto.getBaseCurrencyCode());
        var targetId = findCurrencyIdByCode(dto.getTargetCurrencyCode());

        var rateUsdToBase = findByCurrenciesId(usdId, baseId).getRate();
        var rateUsdToTarget = findByCurrenciesId(usdId, targetId).getRate();

        if (rateUsdToTarget != null && rateUsdToBase != null) {
            return rateUsdToTarget.divide(rateUsdToBase, NUMBER_OF_DIGIT_AFTER_DOT, RoundingMode.CEILING);
        }
        throw new RuntimeException();
    }
}
