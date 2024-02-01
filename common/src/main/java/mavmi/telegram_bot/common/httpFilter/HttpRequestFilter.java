package mavmi.telegram_bot.common.httpFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.httpFilter.exception.HttpRequestFilterException;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.common.cache.AbstractUserCache;
import mavmi.telegram_bot.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Фильтр HTTP запросов.
 * Нужен для заполнения бина {@link UserSession} актуальной информацией
 */
@Slf4j
@Component
@WebFilter
@ConditionalOnProperty(prefix = "service.web-filter", name = "enabled", havingValue = "true")
public class HttpRequestFilter extends HttpFilter {

    public static final String ID_HEADER_NAME = "id";

    private final Cache<? extends AbstractUserCache> cache;

    @Autowired
    private UserSession userSession;

    public HttpRequestFilter(Cache<? extends AbstractUserCache> cache) {
        this.cache = cache;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        setupSession(req);
        super.doFilter(req, res, chain);
    }

    private void setupSession(ServletRequest servletRequest) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String idHeaderValue = httpServletRequest.getHeader(ID_HEADER_NAME);
        if (idHeaderValue == null) {
            String errMsg = "Отсутствует поле хидера " + ID_HEADER_NAME;
            log.error(errMsg);
            throw new HttpRequestFilterException(errMsg);
        }

        userSession.setId(Long.parseLong(idHeaderValue));
    }
}
