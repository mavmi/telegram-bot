package mavmi.telegram_bot.common.httpServlet.httpRequest;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequestWrapper;
import mavmi.telegram_bot.common.httpServlet.exception.HttpServletException;
import org.springframework.util.StreamUtils;

import java.io.*;

public class HttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] requestBody;

    public HttpServletRequest(jakarta.servlet.http.HttpServletRequest request) {
        super(request);

        try {
            InputStream inputStream = request.getInputStream();
            this.requestBody = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            throw new HttpServletException(e);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new HttpRequestInputStream(requestBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}
