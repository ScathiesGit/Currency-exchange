package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.repository.JdbcExchangeRateRepository;
import exchanger.service.CurrencyServiceImpl;
import exchanger.service.ExchangeRateServiceImpl;
import exchanger.service.ExchangeService;
import exchanger.service.ExchangeServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = new ExchangeServiceImpl(
            new ExchangeRateServiceImpl(new JdbcCurrencyRepository(), new JdbcExchangeRateRepository()),
            new CurrencyServiceImpl(new JdbcCurrencyRepository())
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var amount = req.getParameter("amount");
        var baseCode = req.getParameter("from");
        var targetCode = req.getParameter("to");

        if (amount != null && !amount.isEmpty()
                && baseCode != null && !baseCode.isEmpty()
                && targetCode != null && !targetCode.isEmpty()
        ) {
            var result = exchangeService.exchange(baseCode, targetCode, (Double.parseDouble(amount)));
            if (result.isPresent()) {
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, result.get());
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, new IncorrectRequest("Не удалось найти обменный курс"));
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (var output = resp.getWriter()) {
                new ObjectMapper().writeValue(output, new IncorrectRequest("Недостаточно данных для выполнения запроса"));
            }
        }
    }
}
