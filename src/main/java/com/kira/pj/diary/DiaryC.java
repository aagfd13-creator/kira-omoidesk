package com.kira.pj.diary;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(value = "/diary")
public class DiaryC extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession();
        String loginUserId = (String) session.getAttribute("loginUserId");

        // ★ memberId, host_id 둘 다 체크
        String memberId = request.getParameter("memberId");
        String hostId = request.getParameter("host_id");

        // 둘 중 하나라도 있으면 그 값을 주인으로 인정
        String targetId = memberId;
        if (targetId == null || targetId.isEmpty()) {
            targetId = hostId;
        }

        // 여전히 없으면 내 다이어리
        if (targetId == null || targetId.isEmpty()) {
            targetId = loginUserId;
        }

        // ★ [핵심] ownerId를 attribute로 미리 세팅해서 DAO가 파라미터 대신 이 값을 우선 사용하도록 함
        request.setAttribute("ownerId", targetId);

        DiaryDAO.DDAO.getCalendar(request);
        request.getRequestDispatcher("diary/diary.jsp").forward(request, response);
    }
}