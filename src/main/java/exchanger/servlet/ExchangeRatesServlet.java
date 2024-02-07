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

@WebServlet("/exchange-rates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        write(resp, exchangeRateService.findAll(), SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        var base = req.getParameter("baseCurrencyCode");
        var target = req.getParameter("targetCurrencyCode");
        var rate = req.getParameter("rate");

        if (base == null || base.length() != 3 || target == null || target.length() != 3
                || rate == null || rate.isEmpty()
        ) {
            write(resp, new IncorrectRequest("Отсутствует нужное поле формы"), SC_BAD_REQUEST);
            return;
        }

        exchangeRateService.save(base, target, BigDecimal.valueOf(Double.parseDouble(rate)))
                .ifPresentOrElse(
                        result -> write(resp, result, SC_CREATED),
                        () -> write(resp, new IncorrectRequest("Одна из валют не найдена"), SC_NOT_FOUND)
                );
    }
}
