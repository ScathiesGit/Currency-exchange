package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.service.CurrencyService;
import exchanger.service.CurrencyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl(new JdbcCurrencyRepository());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currencyCode = req.getPathInfo().substring(1);
        if (currencyCode.length() == 3) {
            var currency = currencyService.findByCode(currencyCode);
            if (currency.isPresent()) {
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, currency.get());
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (var output = resp.getWriter()) {
                    new ObjectMapper().writeValue(output, new IncorrectRequest("Валюта не найдена"));
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (var output = resp.getWriter()) {
                new ObjectMapper().writeValue(output, new IncorrectRequest("Код валюты должен состоять из трех символов"));
            }
        }
    }
}
