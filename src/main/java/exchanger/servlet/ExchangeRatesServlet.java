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

@WebServlet("/exchange-rates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl(
            new JdbcCurrencyRepository(), new JdbcExchangeRateRepository()
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var output = resp.getWriter()) {
            new ObjectMapper().writeValue(output, exchangeRateService.findAll());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCode = req.getParameter("baseCurrencyCode");
        var targetCode = req.getParameter("targetCurrencyCode");
        var rate = req.getParameter("rate");
        if (baseCode != null && baseCode.length() == 3 && targetCode != null && targetCode.length() == 3
                && rate != null) {
            var result = exchangeRateService.save(baseCode, targetCode, BigDecimal.valueOf(Double.parseDouble(rate)));
            if (result.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, result.get());
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, new IncorrectRequest("Одна из валют не найдена"));
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (var output = resp.getWriter()) {
                new ObjectMapper().writeValue(output, new IncorrectRequest("Отсутствует нужное поле формы"));
            }
        }
    }
}
