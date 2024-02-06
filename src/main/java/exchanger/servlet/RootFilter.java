package exchanger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchanger.dto.IncorrectRequest;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

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
            if (e.getCause() instanceof SQLiteException) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
                try (var output = servletResponse.getWriter()) {
                    new ObjectMapper().writeValue(output, new IncorrectRequest("Такая запись уже существует"));
                }
            } else if (e.getCause() instanceof SQLException) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                try (var output = servletResponse.getWriter()) {
                    new ObjectMapper().writeValue(output, new IncorrectRequest("База данных недоступна"));
                }
            }
        }
    }
}
