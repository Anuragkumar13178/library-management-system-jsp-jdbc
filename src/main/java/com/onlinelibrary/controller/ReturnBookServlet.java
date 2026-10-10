package com.onlinelibrary.controller;
import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import jakarta.servlet.*; import java.io.IOException;
@WebServlet("/member/return") public class ReturnBookServlet extends HttpServlet {protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{try{WebSupport.SERVICE.returnBook(WebSupport.uid(r),Long.parseLong(r.getParameter("transactionId")));s.sendRedirect(r.getContextPath()+"/member/history?message=Book+returned");}catch(Exception e){WebSupport.fail(r,s,e);}}}
