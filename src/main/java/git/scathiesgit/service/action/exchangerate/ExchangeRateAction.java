package git.scathiesgit.service.action.exchangerate;

import git.scathiesgit.dao.currency.CurrencyDao;
import git.scathiesgit.dao.currency.JdbcCurrency;
import git.scathiesgit.dao.exchangerate.ExchangeRateDao;
import git.scathiesgit.dao.exchangerate.JdbcExchangeRate;
import git.scathiesgit.dto.ExchangeRateData;
import git.scathiesgit.entity.ExchangeRate;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ExchangeRateAction implements ExchangeRateActionService {

    private static final CurrencyDao CURRENCY_DAO = new JdbcCurrency();

    private static final ExchangeRateDao EXCHANGE_RATE_DAO = new JdbcExchangeRate();
    @Override
    public List<ExchangeRate> findAll() {
        return EXCHANGE_RATE_DAO.findAll();
    }

    @Override
    public Optional<ExchangeRate> findByCurrencyIDs(ExchangeRateData data) {
        return EXCHANGE_RATE_DAO.findByCurrencyIDs(data);
    }

    @Override
    public OptionalInt save(ExchangeRateData data) {
        return EXCHANGE_RATE_DAO.save(data);
    }

    @Override
    public OptionalInt update(ExchangeRateData data) {
        return EXCHANGE_RATE_DAO.update(data);
    }
}
