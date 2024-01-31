package exchanger.service;

import exchanger.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateService {

    int save(String baseCode, String targetCode, BigDecimal rate);

    boolean update(String baseCode, String targetCode, BigDecimal rate);

    Optional<ExchangeRate> findByCurrency(String baseCode, String targetCode);

    public List<ExchangeRate> findAll();
}
