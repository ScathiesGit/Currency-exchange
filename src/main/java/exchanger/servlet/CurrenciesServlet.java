package exchanger.servlet;

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

import static exchanger.util.ResponseWriter.write;
import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl(new JdbcCurrencyRepository());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        write(resp, currencyService.findAll(), SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        var currency = toCurrency(req);
        currency.setId(currencyService.save(currency));
        write(resp, currency, SC_CREATED);
    }

    private Currency toCurrency(HttpServletRequest req) {
        var currency = Currency.builder()
                .code(req.getParameter("code"))
                .fullName(req.getParameter("name"))
                .sign(req.getParameter("sign"))
                .build();

        if (currency.getCode() == null || currency.getCode().length() != 3
                || currency.getFullName() == null || currency.getSign() == null
        ) {
            throw new IllegalArgumentException("Недостаточно данных для обновления");
        }
        return currency;
    }
}
