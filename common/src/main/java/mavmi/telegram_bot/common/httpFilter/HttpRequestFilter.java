package mavmi.telegram_bot.common.httpFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.auth.AuthCacheService;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.common.cache.userData.UserDataCacheService;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.httpFilter.exception.HttpRequestFilterException;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * HTTP requests filter.
 * Fills {@link UserSession} with actual information
 */
@Slf4j
@Component
@WebFilter
@ConditionalOnProperty(prefix = "service.web-filter", name = "enabled", havingValue = "true")
public class HttpRequestFilter extends HttpFilter {

    public static final String ID_HEADER_NAME = "id";
    public static final String USERNAME_HEADER_NAME = "username";
    public static final String FIRST_NAME_HEADER_NAME = "first-name";
    public static final String LAST_NAME_HEADER_NAME = "last-name";

    private final AbstractService service;
    private final AuthCacheService authCacheService;
    private final UserDataCacheService userDataCacheService;
    private final UserAuthentication userAuthentication;
    private final BOT_NAME botName;

    public HttpRequestFilter(
            AbstractService service,
            AuthCacheService authCacheService,
            UserDataCacheService userDataCacheService,
            UserAuthentication userAuthentication,
            @Value("${service.web-filter.bot-name}") String botName
    ) {
        this.service = service;
        this.authCacheService = authCacheService;
        this.userDataCacheService = userDataCacheService;
        this.userAuthentication = userAuthentication;
        this.botName = BOT_NAME.valueOf(botName);
    }

    @Autowired
    private UserSession userSession;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) req;

        userSession.setId(getId(httpServletRequest));
        userSession.setAccessGranted(getAccessGranted());
        userSession.setCache(getUserDataCache());

        userSession.getCache().setUsername(getUsername(httpServletRequest));
        userSession.getCache().setFirstName(getFirstName(httpServletRequest));
        userSession.getCache().setLastName(getLastName(httpServletRequest));

        super.doFilter(req, res, chain);
    }

    private long getId(HttpServletRequest httpServletRequest) {
        String idHeaderValue = httpServletRequest.getHeader(ID_HEADER_NAME);

        if (idHeaderValue == null) {
            String errMsg = "Отсутствует поле хидера " + ID_HEADER_NAME;
            log.error(errMsg);
            throw new HttpRequestFilterException(errMsg);
        }

        return Long.parseLong(idHeaderValue);
    }

    private boolean getAccessGranted() {
        long userId = userSession.getId();
        Boolean accessGranted = authCacheService.get(userSession.getId());

        if (accessGranted == null) {
            accessGranted = userAuthentication.isPrivilegeGranted(userId, botName);
            authCacheService.put(userId, accessGranted);
        }

        return accessGranted;
    }

    private AbstractUserDataCache getUserDataCache() {
        long userId = userSession.getId();
        AbstractUserDataCache userDataCache = userDataCacheService.get(userId);

        if (userDataCache == null) {
            userDataCache = service.initCache();
            userDataCache.setUserId(userId);
            userDataCacheService.put(userId, userDataCache);
        }

        return userDataCache;
    }

    private String getUsername(HttpServletRequest httpServletRequest) {
        String username = httpServletRequest.getHeader(USERNAME_HEADER_NAME);

        if (username == null) {
            String errMsg = "Отсутствует поле юзернейма " + USERNAME_HEADER_NAME;
            log.error(errMsg);
            throw new HttpRequestFilterException(errMsg);
        }

        return username;
    }

    private String getFirstName(HttpServletRequest httpServletRequest) {
        String firstName = httpServletRequest.getHeader(FIRST_NAME_HEADER_NAME);

        if (firstName == null) {
            String errMsg = "Отсутствует поле имени " + FIRST_NAME_HEADER_NAME;
            log.error(errMsg);
            throw new HttpRequestFilterException(errMsg);
        }

        return firstName;
    }

    private String getLastName(HttpServletRequest httpServletRequest) {
        String lastName = httpServletRequest.getHeader(LAST_NAME_HEADER_NAME);

        if (lastName == null) {
            String errMsg = "Отсутствует поле фамилии " + LAST_NAME_HEADER_NAME;
            log.error(errMsg);
            throw new HttpRequestFilterException(errMsg);
        }

        return lastName;
    }
}
