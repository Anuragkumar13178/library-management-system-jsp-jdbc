package com.onlinelibrary.filter;
import jakarta.servlet.*; import jakarta.servlet.annotation.WebFilter; import jakarta.servlet.http.*; import java.io.IOException;
import com.onlinelibrary.util.AccessPolicy; import com.onlinelibrary.exception.UnauthorizedAccessException;
@WebFilter(urlPatterns={"/member/*","/librarian/*"})
public class AuthorizationFilter implements Filter { public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{HttpServletRequest r=(HttpServletRequest)req;HttpSession session=r.getSession(false);String role=session==null?null:String.valueOf(session.getAttribute("role"));boolean librarian=r.getRequestURI().contains("/librarian/");try{AccessPolicy.requireRole(role,librarian?"LIBRARIAN":"MEMBER");}catch(UnauthorizedAccessException e){((HttpServletResponse)res).sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());return;}chain.doFilter(req,res);} }
