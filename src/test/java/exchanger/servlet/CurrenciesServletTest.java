package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.entity.Currency;
import exchanger.service.CurrencyService;
import exchanger.service.CurrencyServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class CurrenciesServletTest {

    private CurrenciesServlet servlet = new CurrenciesServlet();

    private CurrencyService currencyService;

    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

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

    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @SneakyThrows
    @BeforeEach
    void setUp() {
          currencyService = mock(CurrencyServiceImpl.class);
          var field = servlet.getClass().getDeclaredField("currencyService");
          field.setAccessible(true);
          field.set(servlet, currencyService);
    }

    @SneakyThrows
    @Test
    void whenDoGetThenReturnListOfCurrencies() {
        var currencies = List.of(usd, eur);
        var expected = new ObjectMapper().writeValueAsString(currencies);
        doReturn(currencies).when(currencyService).findAll();
        doReturn(new PrintWriter(output)).when(resp).getWriter();

        servlet.doGet(req, resp);

        assertThat(output.toString()).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    void givenInvalidParamWhenDoPostThenThrowIllegalArgException()  {
        doReturn("").when(req).getParameter("code");
        doReturn("dollar").when(req).getParameter("name");
        doReturn("$").when(req).getParameter("sign");

        assertThatThrownBy(
                () -> servlet.doPost(req, resp)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenExistCurrencyWhenDoPostThenThrowRuntimeException() {
        doReturn("AAA").when(req).getParameter(any());
        doThrow(RuntimeException.class).when(currencyService).save(any());

        assertThatThrownBy(() -> servlet.doPost(req, resp))
                .isInstanceOf(RuntimeException.class);
    }

    @SneakyThrows
    @Test
    void givenValidParamWhenDoPostThenReturnCurrency() {
        doReturn("USD").when(req).getParameter("code");
        doReturn("dollar").when(req).getParameter("name");
        doReturn("$").when(req).getParameter("sign");
        doReturn(1).when(currencyService).save(any());
        doReturn(new PrintWriter(output)).when(resp).getWriter();
        var expected = new ObjectMapper().writeValueAsString(Currency.builder()
                .id(1)
                .code("USD")
                .fullName("dollar")
                .sign("$")
                .build()
        );

        servlet.doPost(req, resp);

        assertThat(output.toString()).isEqualTo(expected);
    }
}