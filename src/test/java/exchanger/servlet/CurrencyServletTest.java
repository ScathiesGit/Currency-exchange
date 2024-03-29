package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.entity.Currency;
import exchanger.service.CurrencyService;
import exchanger.service.CurrencyServiceImpl;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith({MockitoExtension.class})
class CurrencyServletTest {

    @Mock
    private HttpServletResponse resp;
    @Mock
    private HttpServletRequest req;

    private CurrencyServlet servlet = new CurrencyServlet();

    private CurrencyService currencyService;

    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    private Currency usd = Currency.builder()
            .id(1)
            .code("USD")
            .fullName("Dollar")
            .sign("$")
            .build();

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        currencyService = mock(CurrencyServiceImpl.class);
        var field = servlet.getClass().getDeclaredField("currencyService");
        field.setAccessible(true);
        field.set(servlet, currencyService);
    }

    @Test
    void givenExistCurrencyCodeWhenDoGetThenReturnCurrency() throws IOException, ServletException {
        doReturn("/" + usd.getCode()).when(req).getPathInfo();
        doReturn(Optional.of(usd)).when(currencyService).findByCode(usd.getCode());
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(
                new ObjectMapper().writeValueAsString(usd)
        );
    }

    @Test
    void givenNotExistCurrencyCodeWhenDoGetThenNotFound() throws IOException, ServletException {
        doReturn("/" + usd.getCode()).when(req).getPathInfo();
        doReturn(Optional.empty()).when(currencyService).findByCode(usd.getCode());
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(
                new ObjectMapper().writeValueAsString(new IncorrectRequest("Валюта не найдена"))
        );
    }
}