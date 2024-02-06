package exchanger.service;

import exchanger.dto.ExchangeRateDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateService {

    Optional<ExchangeRateDto> save(String baseCode, String targetCode, BigDecimal rate);

    Optional<ExchangeRateDto> update(String baseCode, String targetCode, BigDecimal rate);

    Optional<ExchangeRateDto> findByCurrency(String baseCode, String targetCode);

    List<ExchangeRateDto> findAll();
}
