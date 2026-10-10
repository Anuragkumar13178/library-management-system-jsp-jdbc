package com.onlinelibrary.controller;
import com.onlinelibrary.dao.impl.SearchHistoryDAOImpl; import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import jakarta.servlet.*; import java.io.IOException;
@WebServlet("/member/search-history") public class SearchHistoryServlet extends HttpServlet {protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{try{r.setAttribute("searches",new SearchHistoryDAOImpl().findForMember(WebSupport.uid(r)));WebSupport.view(r,s,"search-history.jsp");}catch(Exception e){WebSupport.fail(r,s,e);}}}
