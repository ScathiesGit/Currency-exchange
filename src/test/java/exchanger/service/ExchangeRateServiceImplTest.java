package exchanger.service;

import exchanger.dto.ExchangeRateDto;
import exchanger.entity.Currency;
import exchanger.entity.ExchangeRate;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith({MockitoExtension.class})
class ExchangeRateServiceImplTest {

    @Mock
    private JdbcCurrencyRepository currencyRepo;
    @Mock
    private JdbcExchangeRateRepository exchangeRateRepo;
    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private final Currency usd = Currency.builder()
            .id(1)
            .code("USD")
            .fullName("US Dollar")
            .sign("$")
            .build();

    private final Currency eur = Currency.builder()
            .id(2)
            .code("EUR")
            .fullName("Euro")
            .sign("€")
            .build();

    private final ExchangeRate usdToEur = ExchangeRate.builder()
            .id(1)
            .baseCurrencyId(usd.getId())
            .targetCurrencyId(eur.getId())
            .rate(BigDecimal.valueOf(.088))
            .build();

    private final ExchangeRateDto expected = ExchangeRateDto.builder()
            .id(usdToEur.getId())
            .baseCurrency(usd)
            .targetCurrency(eur)
            .rate(usdToEur.getRate())
            .build();

    @Test
    void givenExchangeRateNotExistWhenSaveThenReturnExchangeRateDto() {
        doReturn(1).when(exchangeRateRepo).save(any());
        doReturn(Optional.of(usd)).when(currencyRepo).findByCode(usd.getCode());
        doReturn(Optional.of(eur)).when(currencyRepo).findByCode(eur.getCode());

        var actual = exchangeRateService.save(usd.getCode(), eur.getCode(), usdToEur.getRate());

        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get()).isEqualTo(expected)
        );
    }

    @Test
    void givenCurrencyNotExistWhenSaveThenReturnEmpty() {
        doReturn(Optional.empty()).when(currencyRepo).findByCode(usd.getCode());

        var toSave = exchangeRateService.save(usd.getCode(), eur.getCode(), usdToEur.getRate());

        assertThat(toSave).isEmpty();
    }

    @Test
    void givenExistExchangeRateWhenUpdateThenReturnExchangeRateDto() {
        doReturn(Optional.of(usd)).when(currencyRepo).findByCode(usd.getCode());
        doReturn(Optional.of(eur)).when(currencyRepo).findByCode(eur.getCode());
        doReturn(true).when(exchangeRateRepo).update(any());
        doReturn(Optional.of(usdToEur)).when(exchangeRateRepo).findByCurrency(usd.getId(), eur.getId());

        var actual = exchangeRateService.update(usd.getCode(), eur.getCode(), usdToEur.getRate()).get();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenNotExistExchangeRateWhenUpdateThenReturnEmpty() {
        doReturn(Optional.empty()).when(currencyRepo).findByCode(any());

        var actual = exchangeRateService.update(usd.getCode(), eur.getCode(), BigDecimal.ONE);

        assertThat(actual).isEmpty();
    }

    @Test
    void givenExistExchangeRateWhenFindByCurrencyThenReturnExchangeRateDto() {
        doReturn(Optional.of(usd)).when(currencyRepo).findByCode(usd.getCode());
        doReturn(Optional.of(eur)).when(currencyRepo).findByCode(eur.getCode());
        doReturn(Optional.of(usdToEur)).when(exchangeRateRepo).findByCurrency(usd.getId(), eur.getId());

        var actual = exchangeRateService.findByCurrency(usd.getCode(), eur.getCode()).get();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenNotExistExchangeRateWhenFindByCurrencyThenReturnEmpty() {
        doReturn(Optional.of(usd)).when(currencyRepo).findByCode(usd.getCode());
        doReturn(Optional.of(eur)).when(currencyRepo).findByCode(eur.getCode());
        doReturn(Optional.empty()).when(exchangeRateRepo).findByCurrency(usd.getId(), eur.getId());

        var actual = exchangeRateService.findByCurrency(usd.getCode(), eur.getCode());

        assertThat(actual).isEmpty();
    }
}