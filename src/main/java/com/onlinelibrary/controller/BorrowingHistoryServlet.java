package com.onlinelibrary.controller;
import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import jakarta.servlet.*; import java.io.IOException; import java.time.LocalDate;
@WebServlet("/member/history") public class BorrowingHistoryServlet extends HttpServlet {protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{try{r.setAttribute("today",LocalDate.now());r.setAttribute("history",WebSupport.SERVICE.history(WebSupport.uid(r)));WebSupport.view(r,s,"borrow-history.jsp");}catch(Exception e){WebSupport.fail(r,s,e);}}}
