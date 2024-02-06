package exchanger.service;

import exchanger.dto.ExchangeInfo;

import java.util.Optional;

public interface ExchangeService {

    Optional<ExchangeInfo> exchange(String baseCode, String targetCode, double amount);
}
