package com.kira.pj.diary;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/diary-update")
public class DiaryUpdateC extends HttpServlet {

    // 1. [수정 버튼]을 눌렀을 때 -> 수정 폼 화면 띄워주기 (GET)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiaryDAO.DDAO.getDiaryDetail(request); // DB에서 예전 글 가져오기
        request.setAttribute("showMode", "update"); // 'update' 폼 모드로 세팅
        request.getRequestDispatcher("diary/diary.jsp").forward(request, response);
    }

    // 2. [수정완료 버튼]을 눌렀을 때 -> 진짜 DB 내용 바꿔치기 (POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지!
        DiaryDAO.DDAO.updateDiary(request);
    }
}