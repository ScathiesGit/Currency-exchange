package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.ExchangeInfo;
import exchanger.dto.IncorrectRequest;
import exchanger.entity.Currency;
import exchanger.service.ExchangeService;
import exchanger.service.ExchangeServiceImpl;
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
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith({MockitoExtension.class})
class ExchangeServletTest {

    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    private ExchangeServlet servlet = new ExchangeServlet();

    private ExchangeService exchangeService;

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

    private ExchangeInfo exchange = ExchangeInfo.builder()
            .baseCurrency(usd)
            .targetCurrency(eur)
            .rate(BigDecimal.valueOf(.88))
            .amount(10)
            .build();

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        exchangeService = mock(ExchangeServiceImpl.class);
        var field = servlet.getClass().getDeclaredField("exchangeService");
        field.setAccessible(true);
        field.set(servlet, exchangeService);

        exchange.setConvertedAmount(
                exchange.getRate().multiply(BigDecimal.valueOf(exchange.getAmount()))
        );
    }

    @Test
    void givenExistExchangeRateWhenDoGetThenReturnCurrenciesExchange() throws IOException, ServletException {
        doReturn(String.valueOf(exchange.getAmount())).when(req).getParameter("amount");
        doReturn(exchange.getBaseCurrency().getCode()).when(req).getParameter("from");
        doReturn(exchange.getTargetCurrency().getCode()).when(req).getParameter("to");
        doReturn(Optional.of(exchange)).when(exchangeService).exchange(
                usd.getCode(), eur.getCode(), exchange.getAmount()
        );
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(exchange));
    }

    @Test
    void givenNotExistExchangeRateWhenDoGetThenNotFound() throws IOException, ServletException {
        doReturn(String.valueOf(exchange.getAmount())).when(req).getParameter("amount");
        doReturn(exchange.getBaseCurrency().getCode()).when(req).getParameter("from");
        doReturn(exchange.getTargetCurrency().getCode()).when(req).getParameter("to");
        doReturn(Optional.empty()).when(exchangeService).exchange(
                usd.getCode(), eur.getCode(), exchange.getAmount()
        );
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(
                new IncorrectRequest("Не удалось найти обменный курс")
        ));
    }

    @Test
    void givenInvalidParamWhenDoGetThenBadRequest() throws IOException, ServletException {
        doReturn("10").when(req).getParameter("amount");
        doReturn(null).when(req).getParameter("from");
        doReturn("EUR").when(req).getParameter("to");
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(new ObjectMapper().writeValueAsString(
                new IncorrectRequest("Недостаточно данных для выполнения запроса")
        ));
    }
}