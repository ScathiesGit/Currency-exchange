package exchanger.service;

import exchanger.dto.ExchangeInfo;
import exchanger.dto.ExchangeRateDto;
import exchanger.entity.Currency;
import exchanger.entity.ExchangeRate;
import exchanger.repository.CurrencyRepository;
import exchanger.repository.ExchangeRateRepository;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({MockitoExtension.class})
class ExchangeServiceImplTest {

    @InjectMocks
    private ExchangeServiceImpl exchangeService;
    @Mock
    private CurrencyServiceImpl currencyService;
    @Mock
    private ExchangeRateServiceImpl exchangeRateService;

    private final Currency usd = Currency.builder()
            .id(1)
            .code("USD")
            .fullName("US Dollar")
            .sign("$")
            .build();

    private final Currency eur = Currency.builder()
            .id(1)
            .code("EUR")
            .fullName("Euro")
            .sign("€")
            .build();

    private final Currency rub = Currency.builder()
            .id(3)
            .code("RUB")
            .fullName("Russian ruble")
            .sign("P")
            .build();

    private final ExchangeRateDto usdToEur = ExchangeRateDto.builder()
            .id(1)
            .baseCurrency(usd)
            .targetCurrency(eur)
            .rate(BigDecimal.valueOf(0.88))
            .build();

    private final ExchangeRateDto eurToUsd = ExchangeRateDto.builder()
            .id(1)
            .baseCurrency(eur)
            .targetCurrency(usd)
            .rate(
                    BigDecimal.ONE.divide(usdToEur.getRate(), MathContext.DECIMAL32)
            )
            .build();

    private final ExchangeRateDto usdToRub = ExchangeRateDto.builder()
            .id(2)
            .baseCurrency(usd)
            .targetCurrency(rub)
            .rate(BigDecimal.valueOf(91))
            .build();

    private final ExchangeInfo expectedUsdToEur = ExchangeInfo.builder()
            .baseCurrency(usd)
            .targetCurrency(eur)
            .rate(usdToEur.getRate())
            .amount(10)
            .build();

    private final BigDecimal delta = BigDecimal.valueOf(0.00001);

    @BeforeEach
    void initExpected() {
        var amount = expectedUsdToEur.getAmount();
        var rate = expectedUsdToEur.getRate();
        expectedUsdToEur.setConvertedAmount(
                BigDecimal.valueOf(amount).multiply(rate, MathContext.DECIMAL32)
        );
    }

    @Test
    void givenExistDirectRateWhenExchangeThenReturnExchangeInfo() {
        Mockito.doReturn(Optional.of(usdToEur)).when(exchangeRateService).findByCurrency(usd.getCode(), eur.getCode());
        Mockito.doReturn(Optional.of(usd)).when(currencyService).findByCode(usd.getCode());
        Mockito.doReturn(Optional.of(eur)).when(currencyService).findByCode(eur.getCode());

        var actual = exchangeService.exchange(usd.getCode(), eur.getCode(), expectedUsdToEur.getAmount()).get();

        assertThat(actual).isEqualTo(expectedUsdToEur);
    }

    @Test
    void givenExistReverseRateWhenExchangeThenReturnExchangeInfo() {
        Mockito.doReturn(Optional.empty()).when(exchangeRateService)
                .findByCurrency(usd.getCode(), eur.getCode());
        Mockito.doReturn(Optional.of(eurToUsd)).when(exchangeRateService)
                .findByCurrency(eur.getCode(), usd.getCode());
        Mockito.doReturn(Optional.of(usd)).when(currencyService).findByCode(usd.getCode());
        Mockito.doReturn(Optional.of(eur)).when(currencyService).findByCode(eur.getCode());

        var actual = exchangeService.exchange(usd.getCode(), eur.getCode(), expectedUsdToEur.getAmount()).get();

        assertAll(
                () -> assertThat(actual)
                        .extracting("baseCurrency", "targetCurrency", "amount")
                        .containsExactly(
                                expectedUsdToEur.getBaseCurrency(),
                                expectedUsdToEur.getTargetCurrency(),
                                expectedUsdToEur.getAmount()
                        ),
                () -> assertThat(actual.getRate()).isCloseTo(expectedUsdToEur.getRate(), Offset.offset(delta)),
                () -> assertThat(actual.getConvertedAmount()).isCloseTo(
                        expectedUsdToEur.getConvertedAmount(), Offset.offset(delta)
                )
        );
    }

    @Test
    void givenExistCrossRateWhenExchangeThenReturnExchangeInfo() {
        Mockito.when(exchangeRateService.findByCurrency(eur.getCode(), rub.getCode())).thenReturn(Optional.empty());
        Mockito.when(exchangeRateService.findByCurrency(rub.getCode(), eur.getCode())).thenReturn(Optional.empty());
        Mockito.when(currencyService.findByCode(usd.getCode())).thenReturn(Optional.of(usd));
        Mockito.when(exchangeRateService.findByCurrency(usd.getCode(), eur.getCode())).thenReturn(Optional.of(usdToEur));
        Mockito.when(exchangeRateService.findByCurrency(usd.getCode(), rub.getCode())).thenReturn(Optional.of(usdToRub));
        Mockito.when(currencyService.findByCode(eur.getCode())).thenReturn(Optional.of(eur));
        Mockito.when(currencyService.findByCode(rub.getCode())).thenReturn(Optional.of(rub));
        var amount = 10.0;
        var rate = usdToRub.getRate().divide(
                usdToEur.getRate(), MathContext.DECIMAL32
        );
        var expected = ExchangeInfo.builder()
                .baseCurrency(eur)
                .targetCurrency(rub)
                .rate(rate)
                .convertedAmount(rate.multiply(BigDecimal.valueOf(amount)))
                .amount(amount)
                .build();

        var actual = exchangeService.exchange(eur.getCode(), rub.getCode(), amount).get();

        assertAll(
                () -> assertThat(actual)
                        .extracting("baseCurrency", "targetCurrency", "amount")
                        .containsExactly(
                                expected.getBaseCurrency(),
                                expected.getTargetCurrency(),
                                expected.getAmount()
                        ),
                () -> assertThat(actual.getRate()).isCloseTo(expected.getRate(), Offset.offset(delta)),
                () -> assertThat(actual.getConvertedAmount()).isCloseTo(
                        expected.getConvertedAmount(), Offset.offset(delta)
                )
        );
    }

    @Test
    void givenNotExistRateWhenExchangeThenReturnEmpty() {
        Mockito.when(exchangeRateService.findByCurrency(any(), any()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty());
        Mockito.when(currencyService.findByCode(usd.getCode()))
                .thenReturn(Optional.empty());

        var actual = exchangeService.exchange(usd.getCode(), eur.getCode(), 10);

        assertThat(actual).isEmpty();
    }
}