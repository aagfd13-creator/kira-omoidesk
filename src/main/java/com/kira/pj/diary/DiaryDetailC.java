package com.kira.pj.diary;

import com.kira.pj.main.DBManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/diary-detail")
public class DiaryDetailC extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiaryDAO.DDAO.getDiaryDetail(request);
        DiaryDTO diary = (DiaryDTO) request.getAttribute("diary");

        if (diary == null) {
            response.sendError(404, "글이 존재하지 않습니다.");
            return;
        }

        HttpSession session = request.getSession();
        String myId = (String) session.getAttribute("loginUserId");
        String writerId = diary.getId();

        int relation = 0;
        if (myId != null && writerId != null) {
            if (myId.trim().equalsIgnoreCase(writerId.trim())) {
                relation = 2;
            } else {
                Connection con = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                try {
                    con = DBManager.connect();
                    String friendSql = "SELECT * FROM FRIEND_RELATION WHERE " +
                            "((UPPER(TRIM(F_REQUESTER)) = UPPER(?) AND UPPER(TRIM(F_RECEIVER)) = UPPER(?)) OR " +
                            "(UPPER(TRIM(F_REQUESTER)) = UPPER(?) AND UPPER(TRIM(F_RECEIVER)) = UPPER(?))) " +
                            "AND F_STATUS = 1";
                    pstmt = con.prepareStatement(friendSql);
                    pstmt.setString(1, myId.trim());
                    pstmt.setString(2, writerId.trim());
                    pstmt.setString(3, writerId.trim());
                    pstmt.setString(4, myId.trim());

                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        relation = 1;
                    }
                } catch (Exception e) { e.printStackTrace(); }
                finally { DBManager.close(con, pstmt, rs); }
            }
        }

        System.out.println(">>> [상세 로그] 나: " + myId + " | 글쓴이: " + writerId + " | 관계: " + relation);

        int vis = diary.getVisibility();
        boolean canRead = false;
        if (vis == 2) canRead = true;
        else if (vis == 1 && relation >= 1) canRead = true;
        else if (vis == 0 && relation == 2) canRead = true;

        if (canRead) {
            // 1. 댓글 목록 가져오기
            DiaryDAO.DDAO.getReplies(request);

            // ★ [추가] 2. 좋아요 개수 및 나의 좋아요 상태 가져오기
            int likeCount = DiaryDAO.DDAO.getLikeCount(diary.getNo());
            int isLiked = DiaryDAO.DDAO.checkIsLiked(diary.getNo(), myId);

            request.setAttribute("likeCount", likeCount);
            request.setAttribute("isLiked", isLiked);

            request.setAttribute("showMode", "detail");
        } else {
            request.setAttribute("showMode", "list");
            request.setAttribute("errorMsg", "접근 권한이 없습니다. 🔒");
            DiaryDAO.DDAO.getCalendar(request);
        }

        request.getRequestDispatcher("diary/diary.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}