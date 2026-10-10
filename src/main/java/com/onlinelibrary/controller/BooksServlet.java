package com.onlinelibrary.controller;
import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import jakarta.servlet.*; import java.io.IOException;
/** /books used to render the librarian management page for anyone; now it routes by role. */
@WebServlet("/books") public class BooksServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  HttpSession session=r.getSession(false);
  String role=session==null?null:(String)session.getAttribute("role");
  String target="LIBRARIAN".equals(role)?"/librarian/books":"MEMBER".equals(role)?"/member/search":"/login.jsp?error=Please+sign+in";
  s.sendRedirect(r.getContextPath()+target);
 }}
