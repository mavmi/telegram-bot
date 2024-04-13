package mavmi.telegram_bot.common.httpFilter.userSession;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.auth.AuthCacheService;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.common.cache.userData.UserDataCacheService;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.httpFilter.userSession.session.UserSession;
import mavmi.telegram_bot.common.service.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@WebFilter
@ConditionalOnProperty(prefix = "web-filter", name = "enabled", havingValue = "true")
public class UserSessionHttpFilter extends HttpFilter {

    public static final String ID_HEADER_NAME = "id";

    private final AbstractService service;
    private final AuthCacheService authCacheService;
    private final UserDataCacheService userDataCacheService;
    private final UserAuthentication userAuthentication;
    private final BOT_NAME botName;

    public UserSessionHttpFilter(
            AbstractService service,
            AuthCacheService authCacheService,
            UserDataCacheService userDataCacheService,
            UserAuthentication userAuthentication,
            @Value("${web-filter.bot-name}") String botName
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
        setSession(httpServletRequest);
        chain.doFilter(req, res);
    }

    private void setSession(HttpServletRequest httpServletRequest) {
        Long id = getId(httpServletRequest);
        userSession.setId(id);

        if (id != null) {
            userSession.setAccessGranted(getAccessGranted());
            userSession.setCache(getUserDataCache());
        } else {
            userSession.setAccessGranted(false);
            userSession.setCache(null);
        }
    }

    @Nullable
    private Long getId(HttpServletRequest httpServletRequest) {
        String idHeaderValue = httpServletRequest.getHeader(ID_HEADER_NAME);

        if (idHeaderValue == null) {
            log.warn("Missing id header");
            return null;
        }

        try {
            return Long.parseLong(idHeaderValue);
        } catch (NumberFormatException e) {
            log.warn("Bad id valid: {}", idHeaderValue);
            return null;
        }
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

    private UserDataCache getUserDataCache() {
        long userId = userSession.getId();
        UserDataCache userDataCache = userDataCacheService.get(userId);

        if (userDataCache == null) {
            userDataCache = service.initCache();
            userDataCache.setUserId(userId);
            userDataCacheService.put(userId, userDataCache);
        }

        return userDataCache;
    }
}
