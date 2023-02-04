package webService.security.cookie;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import webService.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {
    @Override
    public String resolve(HttpServletRequest request) {
        var cookie = CookieUtils.getCookie(request, CookieConsts.ROTEAuthCookieName);
        if (cookie.isEmpty())
            return null;
        return cookie.get().getValue();
    }
}
