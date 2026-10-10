package com.onlinelibrary.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest r, HttpServletResponse s) throws IOException, ServletException {
        try {
            var u = WebSupport.SERVICE.authenticate(r.getParameter("email"), r.getParameter("password"));
            // Rotate the existing session ID after authentication to prevent session fixation.
            var session = r.getSession(true);
            r.changeSessionId();
            session = r.getSession(false);
            session.setAttribute("userId", u.getId());
            session.setAttribute("name", u.getName());
            session.setAttribute("role", u.getRole());
            s.sendRedirect(r.getContextPath() + ("LIBRARIAN".equals(u.getRole()) ? "/librarian/dashboard" : "/member/dashboard"));
        } catch (com.onlinelibrary.exception.InvalidLoginException e) {
            r.setAttribute("error", "Invalid email or password");
            r.getRequestDispatcher("/login.jsp").forward(r, s);
        } catch (Exception | LinkageError e) {
            // Previously every failure (DB down, wrong DB password, missing tables) showed
            // "Invalid email or password", hiding the real cause. Log it and show a distinct message.
            getServletContext().log("Login failed due to a system error", e);
            r.setAttribute("error", "System error while signing in (" + e.getClass().getSimpleName() + "). Check the server log and database configuration.");
            r.getRequestDispatcher("/login.jsp").forward(r, s);
        }
    }
}
