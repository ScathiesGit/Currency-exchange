package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.ExchangeRateDto;
import exchanger.dto.IncorrectRequest;
import exchanger.entity.Currency;
import exchanger.entity.ExchangeRate;
import exchanger.service.ExchangeRateService;
import exchanger.service.ExchangeRateServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ExchangeRateServletTest {

    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    private ExchangeRateServlet servlet = new ExchangeRateServlet();

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

    @SneakyThrows
    @BeforeEach
    void setUp() {
        exchangeRateService = mock(ExchangeRateServiceImpl.class);
        var field = servlet.getClass().getDeclaredField("exchangeRateService");
        field.setAccessible(true);
        field.set(servlet, exchangeRateService);
    }

    @SneakyThrows
    @Test
    void givenInvalidParamWhenDoGetThenBadRequest() {
        var expected = new ObjectMapper().writeValueAsString(new IncorrectRequest(
                "Пример правильного URL: /exchange-rate/USDEUR Коды валют состоят из трех символов без разделителя"
        ));
        doReturn("/R").when(req).getPathInfo();
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    void givenExistExchangeRateWhenDoGetThenReturnExchangeRate() {
        doReturn("/" + usd.getCode() + eur.getCode()).when(req).getPathInfo();
        doReturn(Optional.of(usdToEur)).when(exchangeRateService).findByCurrency(usd.getCode(), eur.getCode());
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(
                new ObjectMapper().writeValueAsString(usdToEur)
        );
    }

    @SneakyThrows
    @Test
    void giveNotExistExchangeRateWhenDoGetThenNotFound() {
        doReturn("/" + usd.getCode() + eur.getCode()).when(req).getPathInfo();
        doReturn(Optional.empty()).when(exchangeRateService).findByCurrency(usd.getCode(), eur.getCode());
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(
                new IncorrectRequest("Обменный курс не найден")
                )
        );
    }

    @SneakyThrows
    @Test
    void givenInvalidParamWhenDoPostThenBadRequest() {
        doReturn("/" + usd.getCode() + eur.getCode()).when(req).getPathInfo();
        doReturn(null).when(req).getParameter("rate");
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doPost(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(
                new IncorrectRequest("Недостаточно данных для обработки запроса")
        ));
    }

    @SneakyThrows
    @Test
    void givenNotExistExchangeRateWhenDoPostThenNotFound() {
        doReturn("/" + usd.getCode() + eur.getCode()).when(req).getPathInfo();
        doReturn("0.82").when(req).getParameter("rate");
        doReturn(Optional.empty()).when(exchangeRateService).update(any(), any(), any());
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doPost(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(
                new IncorrectRequest("Обменный курс не найден")
        ));
    }

    @SneakyThrows
    @Test
    void givenExistExchangeRateWhenDoPostThenReturnExchangeRate() {
        doReturn("/" + usd.getCode() + eur.getCode()).when(req).getPathInfo();
        doReturn(usdToEur.getRate().toString()).when(req).getParameter("rate");
        doReturn(Optional.of(usdToEur)).when(exchangeRateService).update(
                usd.getCode(), eur.getCode(), usdToEur.getRate()
        );
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doPost(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(usdToEur));
    }
}