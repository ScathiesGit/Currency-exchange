package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.entity.Currency;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.service.CurrencyService;
import exchanger.service.CurrencyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl(new JdbcCurrencyRepository());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currencies = currencyService.findAll();
        try (var output = resp.getWriter()) {
            new ObjectMapper().writeValue(output, currencies);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var code = req.getParameter("code");
        var name = req.getParameter("name");
        var sign = req.getParameter("sign");
        var currency = Currency.builder()
                .code(code)
                .fullName(name)
                .sign(sign)
                .build();

        if (code != null && code.length() == 3
                && name != null && !name.isEmpty()
                && sign != null && !sign.isEmpty()
        ) {
            currency.setId(currencyService.save(currency));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            try (var output = resp.getWriter()) {
                new ObjectMapper().writeValue(output, currency);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (var output = resp.getWriter()) {
                new ObjectMapper().writeValue(output, new IncorrectRequest("Отсутствует нужное поле формы"));
            }
        }
    }
}
