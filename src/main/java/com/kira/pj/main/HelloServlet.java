package com.kira.pj.main;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "Main", value = "/main")

public class HelloServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // 캐시 방지
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        Object loginUserPk = null;

        if (session != null) {
            loginUserPk = session.getAttribute("loginUserPk");
        }

        // 로그인 안 된 상태면 login으로 보냄
        if (loginUserPk == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 로그인 된 상태면 main 출력
        request.setAttribute("content", "/main.jsp");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}