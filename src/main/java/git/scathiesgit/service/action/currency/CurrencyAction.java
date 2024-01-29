package git.scathiesgit.service.action.currency;

import git.scathiesgit.dao.currency.CurrencyDao;
import git.scathiesgit.dao.currency.JdbcCurrency;
import git.scathiesgit.dto.CurrencyData;
import git.scathiesgit.entity.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyAction implements CurrencyActionService {

    private static final CurrencyDao CURRENCY_DAO = new JdbcCurrency();

    @Override
    public Optional<Currency> findByCode(String code) {
        return CURRENCY_DAO.findByCode(code);
    }

    @Override
    public Optional<Currency> findById(int id) {
        return CURRENCY_DAO.findById(id);
    }

    @Override
    public List<Currency> findAll() {
        return CURRENCY_DAO.findAll();
    }

    @Override
    public int save(CurrencyData data) {
        return CURRENCY_DAO.save(data);
    }
}
