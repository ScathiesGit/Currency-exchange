package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;
import exchanger.service.ExchangeRateService;
import exchanger.service.ExchangeRateServiceImpl;
import exchanger.util.ResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

import static exchanger.util.ResponseWriter.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchange-rate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl(
            new JdbcCurrencyRepository(), new JdbcExchangeRateRepository()
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currencyCodes = req.getPathInfo().substring(1);
        if (currencyCodes.length() != 6) {
            write(resp, new IncorrectRequest(
                    "Пример правильного URL: /exchange-rate/USDEUR Коды валют состоят из трех символов без разделителя"
            ), SC_BAD_REQUEST);
            return;
        }
        exchangeRateService.findByCurrency(currencyCodes.substring(0, 3), currencyCodes.substring(3))
                .ifPresentOrElse(
                        result -> write(resp, result, SC_OK),
                        () -> write(resp, new IncorrectRequest("Обменный курс не найден"), SC_NOT_FOUND)
                );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        var currencyCodes = req.getPathInfo().substring(1);
        var rate = req.getParameter("rate");

        if (currencyCodes.length() != 6 || rate == null || rate.isEmpty()) {
            write(resp, new IncorrectRequest("Недостаточно данных для обработки запроса"), SC_BAD_REQUEST);
            return;
        }

        exchangeRateService.update(
                currencyCodes.substring(0, 3), currencyCodes.substring(3),
                BigDecimal.valueOf(Double.parseDouble(rate))
        ).ifPresentOrElse(
                result -> write(resp, result, SC_OK),
                () -> write(resp, new IncorrectRequest("Обменный курс не найден"), SC_NOT_FOUND)
        );
    }
}
