package com.onlinelibrary.filter;
import jakarta.servlet.*; import jakarta.servlet.annotation.WebFilter; import jakarta.servlet.http.*; import java.io.IOException;
@WebFilter(urlPatterns={"/member/*","/librarian/*"})
public class AuthenticationFilter implements Filter { public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{HttpServletRequest r=(HttpServletRequest)req;HttpSession s=r.getSession(false);if(s==null||s.getAttribute("userId")==null){((HttpServletResponse)res).sendRedirect(r.getContextPath()+"/login.jsp?error=Please+sign+in");return;}chain.doFilter(req,res);} }
