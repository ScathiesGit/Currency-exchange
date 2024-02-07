package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.ExchangeRateDto;
import exchanger.dto.IncorrectRequest;
import exchanger.entity.Currency;
import exchanger.service.ExchangeRateService;
import exchanger.service.ExchangeRateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith({MockitoExtension.class})
class ExchangeRatesServletTest {

    @Mock
    private HttpServletResponse resp;
    @Mock
    private HttpServletRequest req;

    private ExchangeRatesServlet servlet;

    private ExchangeRateService exchangeRateService;

    private ByteArrayOutputStream output = new ByteArrayOutputStream();

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
            .rate(BigDecimal.valueOf(1.22))
            .build();

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        exchangeRateService = mock(ExchangeRateServiceImpl.class);
        servlet = new ExchangeRatesServlet();
        var field = servlet.getClass().getDeclaredField("exchangeRateService");
        field.setAccessible(true);
        field.set(servlet, exchangeRateService);
    }

    @Test
    void whenDoGetThenReturnListExchangeRates() throws IOException, ServletException {
        var exchangeRates = List.of(eurToUsd, usdToEur);
        doReturn(exchangeRates).when(exchangeRateService).findAll();
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(exchangeRates));
    }

    @Test
    void givenInvalidParamWhenDoPostThenBadRequest() throws IOException {
        doReturn(usd.getCode()).when(req).getParameter("baseCurrencyCode");
        doReturn(usdToEur.getTargetCurrency().getCode()).when(req).getParameter("targetCurrencyCode");
        doReturn(null).when(req).getParameter("rate");
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doPost(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(
                new IncorrectRequest("Отсутствует нужное поле формы")
        ));
    }

    @Test
    void givenNotExistExchangeRateWhenDoPostThenReturnExchangeRate() throws IOException {
        doReturn(usd.getCode()).when(req).getParameter("baseCurrencyCode");
        doReturn(usdToEur.getTargetCurrency().getCode()).when(req).getParameter("targetCurrencyCode");
        doReturn(usdToEur.getRate().toString()).when(req).getParameter("rate");
        doReturn(Optional.of(usdToEur)).when(exchangeRateService).save(
                usdToEur.getBaseCurrency().getCode(), usdToEur.getTargetCurrency().getCode(), usdToEur.getRate()
        );
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doPost(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(usdToEur));
    }

    @Test
    void givenNotExistCurrencyWHenDoPostThenNotFound() throws IOException {
        doReturn(usd.getCode()).when(req).getParameter("baseCurrencyCode");
        doReturn(usdToEur.getTargetCurrency().getCode()).when(req).getParameter("targetCurrencyCode");
        doReturn(usdToEur.getRate().toString()).when(req).getParameter("rate");
        doReturn(Optional.empty()).when(exchangeRateService).save(
                usdToEur.getBaseCurrency().getCode(), usdToEur.getTargetCurrency().getCode(), usdToEur.getRate()
        );
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doPost(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(
                new IncorrectRequest("Одна из валют не найдена")
        ));
    }
}