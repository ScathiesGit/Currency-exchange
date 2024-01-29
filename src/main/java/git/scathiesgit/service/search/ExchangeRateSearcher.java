package git.scathiesgit.service.search;

import git.scathiesgit.dto.ExchangeData;
import git.scathiesgit.dto.ExchangeRateData;
import git.scathiesgit.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateSearcher {

    Optional<BigDecimal> findRate(String baseCode, String targetCode);
}
