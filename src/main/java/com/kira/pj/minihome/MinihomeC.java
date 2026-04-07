package com.kira.pj.minihome;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/minihome")
public class MinihomeC extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 클라이언트가 접속하려는 대상의 아이디 (예: ?id=test123)
        String targetId = request.getParameter("id");

        if (targetId == null || targetId.trim().isEmpty()) {
            response.sendRedirect("error.jsp");
            return;
        }

        // 2. DAO를 통해 아이디에 해당하는 미니홈피 정보 추출 (내부적으로 JOIN 발생)
        MinihomeDAO dao = new MinihomeDAO();
        MinihomeDTO homeData = dao.getMinihomeByUserId(targetId);

        if (homeData == null) {
            // 해당 아이디를 가진 회원이 없거나, 미니홈피가 생성되지 않은 경우
            response.sendRedirect("notFound.jsp");
            return;
        }

        // 3. JSP에서 화면을 그리기 위해 데이터 전송
        request.setAttribute("homeInfo", homeData);
        // 방명록 등에서 주인을 식별해야 하므로 targetId(u_id)를 계속 유지시킴
        request.setAttribute("ownerId", targetId);

        request.getRequestDispatcher("minihome_main.jsp").forward(request, response);
    }
}