package com.kira.pj.diary;

import com.kira.pj.friend.FriendDAO;
import com.kira.pj.friend.FriendDTO;
import com.kira.pj.main.DBManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DiaryDAO {

    public static final DiaryDAO DDAO = new DiaryDAO();
    public Connection con = null;

    private DiaryDAO() {
    }

    // [보조 메서드] ID를 가지고 해당 유저의 PK를 찾아오는 로직
    // ★ private → public 으로 변경 (DiaryDetailC에서도 사용)
    public String getUserPkById(String userId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBManager.connect();
            String sql = "SELECT u_pk FROM userReg WHERE u_id = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId.trim());
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("u_pk");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }
        return null;
    }

    public void getCalendar(HttpServletRequest req) {
        Calendar cal = Calendar.getInstance();
        String y = req.getParameter("y");
        String m = req.getParameter("m");
        String d = req.getParameter("d");
        String mode = req.getParameter("mode");

        HttpSession session = req.getSession();
        String myId = (String) session.getAttribute("loginUserId");
        String myPk = (String) session.getAttribute("loginUserPk");

        // ★ [핵심 수정] 서블릿(DiaryC)이 미리 세팅한 ownerId attribute를 최우선으로 사용
        // 없으면 파라미터에서 읽고, 그것도 없으면 내 ID 사용
        String targetId = (String) req.getAttribute("ownerId");
        if (targetId == null || targetId.isEmpty()) {
            String memberId = req.getParameter("memberId");
            if (memberId == null || memberId.isEmpty()) {
                memberId = req.getParameter("host_id");
            }
            targetId = (memberId == null || memberId.isEmpty()) ? myId : memberId;
        }

        req.setAttribute("ownerId", targetId);

        int year = (y == null || y.equals("")) ? cal.get(Calendar.YEAR) : Integer.parseInt(y);
        int month = (m == null || m.equals("")) ? cal.get(Calendar.MONTH) : Integer.parseInt(m) - 1;

        cal.set(year, month, 1);
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH);
        int startDay = cal.get(Calendar.DAY_OF_WEEK);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        req.setAttribute("startDay", startDay);
        req.setAttribute("lastDay", lastDay);
        req.setAttribute("curYear", curYear);
        req.setAttribute("curMonth", curMonth + 1);
        req.setAttribute("prevYear", (curMonth == 0) ? curYear - 1 : curYear);
        req.setAttribute("prevMonth", (curMonth == 0) ? 12 : curMonth);
        req.setAttribute("nextYear", (curMonth == 11) ? curYear + 1 : curYear);
        req.setAttribute("nextMonth", (curMonth == 11) ? 1 : curMonth + 2);

        String showMode = "calendar";
        if ("write".equals(mode)) {
            showMode = "write";
            req.setAttribute("selectedDay", d);
        } else if (d != null && !d.equals("")) {
            showMode = "list";
            req.setAttribute("selectedDay", d);
        }
        req.setAttribute("showMode", showMode);

        if ("list".equals(showMode)) {
            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            ArrayList<DiaryDTO> posts = new ArrayList<>();

            try {
                con = DBManager.connect();

                // ★ [관계 파악 로직] 내 PK와 주인의 PK로 일촌 여부 판단
                int relation = 0; // 0:남남, 1:일촌, 2:본인
                if (myId != null && myId.equals(targetId)) {
                    relation = 2; // 나 자신
                } else {
                    // ★ 주인의 ID로 DB에서 실시간 PK 조회
                    String targetPk = getUserPkById(targetId);
                    if (myPk != null && targetPk != null) {
                        FriendDAO fdao = new FriendDAO();
                        FriendDTO fdto = fdao.checkRelation(myPk, targetPk);
                        if (fdto != null && fdto.getF_status() == 1) {
                            relation = 1; // 일촌 확인 완료
                        }
                    }
                }

                String formattedMonth = String.format("%02d", curMonth + 1);
                String formattedDay = String.format("%02d", Integer.parseInt(d));
                String fullDate = curYear + "-" + formattedMonth + "-" + formattedDay;

                // ★ [중요] 쿼리문: targetId의 글을 조회하고 관계별 필터링 적용
                String sql = "SELECT * FROM diary_test WHERE TO_CHAR(d_date, 'YYYY-MM-DD') = ? AND d_id = ? ";
                if (relation == 2) sql += "AND d_visibility IN (0, 1, 2) ";
                else if (relation == 1) sql += "AND d_visibility IN (1, 2) ";
                else sql += "AND d_visibility = 2 ";
                sql += "ORDER BY d_no DESC";

                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, fullDate);
                pstmt.setString(2, targetId);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    DiaryDTO dto = new DiaryDTO();
                    dto.setNo(rs.getInt("d_no"));
                    dto.setId(rs.getString("d_id").trim());
                    dto.setTitle(rs.getString("d_title"));
                    dto.setTxt(rs.getString("d_txt"));
                    dto.setVisibility(rs.getInt("d_visibility"));
                    posts.add(dto);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DBManager.close(con, pstmt, rs);
            }
            req.setAttribute("posts", posts);
        }
    }

    public void insertDiary(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBManager.connect();
            String sql = "INSERT INTO diary_test VALUES (diary_test_seq.nextval, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, SYSDATE, ?)";
            pstmt = con.prepareStatement(sql);
            String year = req.getParameter("d_year");
            String month = req.getParameter("d_month");
            String date = req.getParameter("d_date");
            String title = req.getParameter("d_title");
            String txt = req.getParameter("d_txt");
            String visibility = req.getParameter("d_visibility");
            HttpSession session = req.getSession();
            String id = (String) session.getAttribute("loginUserId");
            if (id == null || id.isEmpty()) return;
            String formattedMonth = String.format("%02d", Integer.parseInt(month));
            String formattedDay = String.format("%02d", Integer.parseInt(date));
            String fullDate = year + "-" + formattedMonth + "-" + formattedDay;
            pstmt.setString(1, id);
            pstmt.setString(2, fullDate);
            pstmt.setString(3, title);
            pstmt.setString(4, txt);
            pstmt.setInt(5, Integer.parseInt(visibility));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    public void getDiaryDetail(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBManager.connect();
            String no = req.getParameter("no");
            String y = req.getParameter("y");
            String m = req.getParameter("m");
            String d = req.getParameter("d");
            String memberId = req.getParameter("memberId");
            String sql = "SELECT * FROM diary_test WHERE d_no = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(no));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                DiaryDTO dto = new DiaryDTO();
                dto.setNo(rs.getInt("d_no"));
                dto.setId(rs.getString("d_id").trim());
                dto.setTitle(rs.getString("d_title"));
                dto.setTxt(rs.getString("d_txt"));
                dto.setDate(rs.getDate("d_date"));
                dto.setVisibility(rs.getInt("d_visibility"));
                req.setAttribute("diary", dto);
            }
            req.setAttribute("curYear", y);
            req.setAttribute("curMonth", m);
            req.setAttribute("selectedDay", d);
            req.setAttribute("ownerId", (memberId == null) ? "" : memberId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }
    }

    public void updateDiary(HttpServletRequest request) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBManager.connect();
            String no = request.getParameter("no");
            String title = request.getParameter("d_title");
            String txt = request.getParameter("d_txt");
            String visibility = request.getParameter("d_visibility");
            String sql = "UPDATE diary_test SET d_title = ?, d_txt = ?, d_visibility = ? WHERE d_no = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, txt);
            pstmt.setInt(3, Integer.parseInt(visibility));
            pstmt.setInt(4, Integer.parseInt(no));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    public void deleteDiary(HttpServletRequest request) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBManager.connect();
            String no = request.getParameter("no");
            String sql = "DELETE FROM diary_test WHERE d_no = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(no));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    public void insertReply(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBManager.connect();
            String sql = "INSERT INTO diary_reply VALUES (diary_reply_seq.nextval, ?, ?, ?, SYSDATE)";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(req.getParameter("d_no")));
            pstmt.setString(2, (String) req.getSession().getAttribute("loginUserId"));
            pstmt.setString(3, req.getParameter("r_txt"));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    public void getReplies(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBManager.connect();
            String sql = "SELECT * FROM diary_reply WHERE d_no = ? ORDER BY r_date ASC";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(req.getParameter("no")));
            rs = pstmt.executeQuery();
            ArrayList<ReplyDTO> replies = new ArrayList<>();
            while (rs.next()) {
                replies.add(new ReplyDTO(rs.getInt("r_no"), rs.getInt("d_no"), rs.getString("r_id"), rs.getString("r_txt"), rs.getDate("r_date")));
            }
            req.setAttribute("replies", replies);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }
    }

    public void deleteReply(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBManager.connect();
            pstmt = con.prepareStatement("DELETE FROM diary_reply WHERE r_no = ?");
            pstmt.setInt(1, Integer.parseInt(req.getParameter("r_no")));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }
}