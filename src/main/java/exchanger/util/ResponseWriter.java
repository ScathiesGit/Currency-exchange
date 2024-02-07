package exchanger.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseWriter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> void write(HttpServletResponse resp, T value, int statusCode) {
        resp.setStatus(statusCode);
        try (var output = resp.getWriter()) {
            MAPPER.writeValue(output, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
