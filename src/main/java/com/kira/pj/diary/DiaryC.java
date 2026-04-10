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

        // 1. 세션에서 로그인한 내 ID 가져오기
        HttpSession session = request.getSession();
        String loginUserId = (String) session.getAttribute("loginUserId");

        // 2. 파라미터로 넘어온 memberId(다이어리 주인) 확인
        String memberId = request.getParameter("memberId");

        // 3. memberId가 없으면 내 다이어리, 있으면 해당 유저의 다이어리 조회
        // DAO의 getCalendar 메서드 내부에서 이 targetId를 사용해 DB 조회를 해야 합니다.
        String targetId = (memberId == null || memberId.isEmpty()) ? loginUserId : memberId;

        // DAO에 targetId를 세팅 (DAO 로직에 따라 request에 담아 보내는 방식 유지)
        request.setAttribute("ownerId", targetId);

        DiaryDAO.DDAO.getCalendar(request);
        request.getRequestDispatcher("diary/diary.jsp").forward(request, response);
    }
}