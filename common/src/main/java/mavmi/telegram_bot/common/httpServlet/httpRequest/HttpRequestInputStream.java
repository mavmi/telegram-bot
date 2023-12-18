package mavmi.telegram_bot.common.httpServlet.httpRequest;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpRequestInputStream extends ServletInputStream {

    private final InputStream requestBody;

    private ReadListener readListener;

    public HttpRequestInputStream(byte[] requestBody) {
        this.requestBody = new ByteArrayInputStream(requestBody);
    }

    @Override
    public int read() throws IOException {
        return requestBody.read();
    }

    @Override
    public boolean isFinished() {
        try {
            return requestBody.available() == 0;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        return super.readLine(b, off, len);
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
    }
}
