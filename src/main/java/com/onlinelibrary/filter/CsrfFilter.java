package com.onlinelibrary.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/** Protects authenticated state-changing requests with a per-session CSRF token. */
@WebFilter(urlPatterns={"/member/*", "/librarian/*", "/logout"})
public class CsrfFilter implements Filter {
    private static final String TOKEN = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String token(HttpSession session) {
        Object existing=session.getAttribute(TOKEN);
        if(existing instanceof String value && !value.isBlank()) return value;
        byte[] bytes=new byte[32];
        RANDOM.nextBytes(bytes);
        String value=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(TOKEN,value);
        return value;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r=(HttpServletRequest)req;
        HttpServletResponse s=(HttpServletResponse)res;
        HttpSession session=r.getSession(false);
        if(session==null || session.getAttribute("userId")==null){
            chain.doFilter(req,res);
            return;
        }
        String expected=token(session);
        if("POST".equalsIgnoreCase(r.getMethod())) {
            String supplied=r.getParameter(TOKEN);
            if(supplied==null || !java.security.MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8), supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                s.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid CSRF token");
                return;
            }
        }
        r.setAttribute(TOKEN, expected);
        chain.doFilter(req,res);
    }
}
