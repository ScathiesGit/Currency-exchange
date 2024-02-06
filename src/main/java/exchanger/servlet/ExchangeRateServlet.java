package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;
import exchanger.service.ExchangeRateService;
import exchanger.service.ExchangeRateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange-rate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl(
            new JdbcCurrencyRepository(), new JdbcExchangeRateRepository()
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currencyCodes = req.getPathInfo().substring(1);

        if (currencyCodes.length() == 6) {
            var result = exchangeRateService.findByCurrency(
                    currencyCodes.substring(0, 3), currencyCodes.substring(3)
            );

            if (result.isPresent()) {
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, result.get());
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, new IncorrectRequest("Обменный курс не найден"));
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (var output = resp.getWriter()) {
                new ObjectMapper().writeValue(output, new IncorrectRequest(
                        "Пример правильного URL: /exchange-rate/USDEUR Коды валют состоят из трех символов без разделителя"
                ));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currencyCodes = req.getPathInfo().substring(1);
        var rate = req.getParameter("rate");
        if (currencyCodes.length() == 6 && rate != null && !rate.isEmpty()) {
            var result = exchangeRateService.update(
                    currencyCodes.substring(0, 3), currencyCodes.substring(3),
                    BigDecimal.valueOf(Double.parseDouble(rate))
                    );
            if (result.isPresent()) {
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, result.get());
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, new IncorrectRequest("Обменный курс не найден"));
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (var output = resp.getWriter()) {
                new ObjectMapper().writeValue(output, new IncorrectRequest("Недостаточно данных для обработки запроса"));
            }
        }
    }
}
