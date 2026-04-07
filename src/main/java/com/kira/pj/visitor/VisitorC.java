package com.kira.pj.visitor;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/visitor")
public class VisitorC extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ajax = request.getParameter("ajax");
        String reqType = request.getParameter("reqType");
        String pStr = request.getParameter("p");
        int p = (pStr == null) ? 1 : Integer.parseInt(pStr);

        // TODO: 향후 단일 서비스가 아닌 다중 회원 서비스로 확장할 경우,
        // 하드코딩된 "DongMin"을 request.getParameter("ownerId") 등으로 받아와야 합니다.
        String ownerId = "DongMin";

        if ("json".equals(reqType)) {
            VisitorDAO dao = new VisitorDAO();
            List<VisitorDTO> list = dao.getVisitorsByPage(ownerId, p);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("visitorList", list);
            resultMap.put("currentPage", p);

            Gson gson = new Gson();
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(gson.toJson(resultMap));

        } else if ("recent".equals(reqType)) {
            VisitorDAO dao = new VisitorDAO();
            List<VisitorDTO> recentList = dao.getRecentVisitors(ownerId);

            Gson gson = new Gson();
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(gson.toJson(recentList));

        } else if ("true".equals(ajax)) {
            request.getRequestDispatcher("visitor/visitor.jsp").forward(request, response);

        } else {
            request.setAttribute("content", "visitor/visitor.jsp");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. 프론트엔드에서 URLSearchParams로 보낸 데이터 추출
        String visitorName = request.getParameter("visitorName");
        String visitorEmojiStr = request.getParameter("visitorEmoji");
        String ownerId = request.getParameter("ownerId");

        // 2. 파라미터 누락 방지 (유효성 검증)
        if (visitorName != null && !visitorName.trim().isEmpty() &&
                visitorEmojiStr != null && ownerId != null) {

            try {
                // 3. String으로 넘어온 이모지 값을 int로 형변환
                int emojiInt = Integer.parseInt(visitorEmojiStr);

                VisitorDTO dto = new VisitorDTO();
                dto.setV_writer_id(visitorName.trim());
                dto.setV_owner_id(ownerId);
                dto.setV_emoji(emojiInt);

                VisitorDAO dao = new VisitorDAO();

                // 4. 기존 insertVisitor가 아닌 upsertVisitor 호출
                int result = dao.upsertVisitor(dto);

                // 5. DB 반영 결과에 따른 응답 분기
                if (result > 0) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print("success");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }

            } catch (NumberFormatException e) {
                // 이모지 값이 숫자가 아닌 문자로 넘어왔을 때의 예외 처리
                System.err.println("이모지 파라미터 변환 오류: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            } catch (Exception e) {
                // DB 연결 실패 또는 MERGE INTO 구문 에러 시 클라이언트에 서버 에러 반환
                System.err.println("방명록 Upsert DB 처리 중 오류: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            // 필수 파라미터가 비어있을 때
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}