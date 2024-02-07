package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.repository.JdbcCurrencyRepository;
import exchanger.service.CurrencyService;
import exchanger.service.CurrencyServiceImpl;
import exchanger.util.ResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static exchanger.util.ResponseWriter.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        currencyService.findByCode(req.getPathInfo().substring(1))
                .ifPresentOrElse(
                        currency -> write(resp, currency, SC_OK),
                        () -> write(resp, new IncorrectRequest("Валюта не найдена"), SC_NOT_FOUND)
                );
    }
}
