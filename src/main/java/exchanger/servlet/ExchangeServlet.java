package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;
import exchanger.service.CurrencyServiceImpl;
import exchanger.service.ExchangeRateServiceImpl;
import exchanger.service.ExchangeService;
import exchanger.service.ExchangeServiceImpl;
import exchanger.util.ResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static exchanger.util.ResponseWriter.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var amount = req.getParameter("amount");
        var baseCode = req.getParameter("from");
        var targetCode = req.getParameter("to");

        if (amount == null || amount.isEmpty()
                || baseCode == null || baseCode.length() != 3
                || targetCode == null || targetCode.length() != 3
        ) {
            write(resp, new IncorrectRequest("Недостаточно данных для выполнения запроса"), SC_BAD_REQUEST);
            return;
        }

        exchangeService.exchange(baseCode, targetCode, (Double.parseDouble(amount)))
                .ifPresentOrElse(
                        result -> write(resp, result, SC_OK),
                        () -> write(resp, new IncorrectRequest("Не удалось найти обменный курс"), SC_NOT_FOUND)
                );
    }
}
