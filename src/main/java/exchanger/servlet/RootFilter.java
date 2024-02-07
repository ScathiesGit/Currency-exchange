package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import exchanger.util.ResponseWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static exchanger.util.ResponseWriter.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class RootFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());

        servletResponse.setContentType("application/json");
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (RuntimeException e) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            if (e.getCause() instanceof SQLiteException) {
                write(httpResponse, new IncorrectRequest("Такая запись уже существует"), SC_CONFLICT);
            } else if (e.getCause() instanceof SQLException) {
                write(httpResponse, new IncorrectRequest("База данных недоступна"), SC_INTERNAL_SERVER_ERROR);
            } else if (e instanceof IllegalArgumentException) {
                write(httpResponse, new IncorrectRequest(e.getMessage()), SC_BAD_REQUEST);
            }
        }
    }
}
