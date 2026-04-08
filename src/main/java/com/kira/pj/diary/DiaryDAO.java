package com.kira.pj.diary;

import com.kira.pj.main.DBManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

public class DiaryDAO {

    public static final DiaryDAO DDAO = new DiaryDAO();
    public Connection con = null;

    private DiaryDAO() {
    }

    public void getCalendar(HttpServletRequest req) {

        // 1. 현재 날짜 구하기
        Calendar cal = Calendar.getInstance();

        // 2. 사용자가 보낸 년도(y), 월(m) 파라미터 받기
        String y = req.getParameter("y");
        String m = req.getParameter("m");

        // 파라미터가 없으면(null) 현재 날짜의 년/월을 사용
        int year = (y == null) ? cal.get(Calendar.YEAR) : Integer.parseInt(y);
        int month = (m == null) ? cal.get(Calendar.MONTH) : Integer.parseInt(m) - 1;

        // 3. 계산을 위해 Calendar 객체 설정
        cal.set(year, month, 1);

        // 다시 정확한 년/월을 뽑음 (13월 입력 시 다음 해 1월로 자동 계산됨)
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH); // 0~11

        int startDay = cal.get(Calendar.DAY_OF_WEEK); // 1일의 요일 (1:일 ~ 7:토)
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 4. JSP로 보낼 데이터 바구니(request)에 담기
        req.setAttribute("startDay", startDay);
        req.setAttribute("lastDay", lastDay);
        req.setAttribute("curYear", curYear);
        req.setAttribute("curMonth", curMonth + 1);

        // [이전/다음 달 화살표 링크용 데이터]
        req.setAttribute("prevYear", (curMonth == 0) ? curYear - 1 : curYear);
        req.setAttribute("prevMonth", (curMonth == 0) ? 12 : curMonth);
        req.setAttribute("nextYear", (curMonth == 11) ? curYear + 1 : curYear);
        req.setAttribute("nextMonth", (curMonth == 11) ? 1 : curMonth + 2);

        // 5. 모드 제어 (d: 날짜 클릭, mode: 글쓰기)
        String d = req.getParameter("d");
        String mode = req.getParameter("mode");

        if ("write".equals(mode)) {
            req.setAttribute("showMode", "write"); // 글쓰기 모드
            req.setAttribute("selectedDay", d);
        } else if (d != null) {
            req.setAttribute("showMode", "list");
            req.setAttribute("selectedDay", d);

            // --- 가짜 데이터 지우고 진짜 DB 연동 시작 ---
            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            ArrayList<DiaryDTO> posts = new ArrayList<>();

            try {
                con = DBManager.connect();

                // 클릭한 날짜를 DB 날짜 형식(YYYY-MM-DD)에 맞게 조립
                String formattedMonth = String.format("%02d", curMonth + 1);
                String formattedDay = String.format("%02d", Integer.parseInt(d));
                String fullDate = curYear + "-" + formattedMonth + "-" + formattedDay;

                // TO_CHAR를 써서 해당 날짜에 쓴 일기만 최신순(d_no DESC)으로 가져옴
                String sql = "SELECT * FROM diary_test WHERE TO_CHAR(d_date, 'YYYY-MM-DD') = ? ORDER BY d_no DESC";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, fullDate);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    DiaryDTO dto = new DiaryDTO();
                    dto.setNo(rs.getInt("d_no"));
                    dto.setTitle(rs.getString("d_title"));
                    dto.setTxt(rs.getString("d_txt"));

                    // ★ 목록에서도 나중에 쓸 수 있게 미리 담아둠!
                    dto.setVisibility(rs.getInt("d_visibility"));

                    posts.add(dto);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DBManager.close(con, pstmt, rs);
            }
            req.setAttribute("posts", posts);
        } else {
            req.setAttribute("showMode", "calendar");
        }
    }

    // 전체조회
    public void selectAllDiary(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM diary_test ORDER BY d_date DESC";
        ArrayList<DiaryDTO> diaries = new ArrayList<>();
        try {
            con = DBManager.connect();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                DiaryDTO dto = new DiaryDTO();
                dto.setNo(rs.getInt("d_no"));
                dto.setId(rs.getString("d_id"));
                dto.setDate(rs.getDate("d_date"));
                dto.setTitle(rs.getString("d_title"));
                dto.setTxt(rs.getString("d_txt"));

                // ★ 전체 조회에서도 공개 설정값 담아둠!
                dto.setVisibility(rs.getInt("d_visibility"));

                diaries.add(dto);
            }
            req.setAttribute("diaries", diaries);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }
    }

    // 일기 등록 기능 (Create)
    public void insertDiary(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBManager.connect();

            // ★ SQL 수정: d_visibility 값을 넣을 물음표(?) 하나 추가!
            String sql = "INSERT INTO diary_test VALUES (diary_test_seq.nextval, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, SYSDATE, ?)";
            pstmt = con.prepareStatement(sql);

            // 파라미터 꺼내기 (visibility 포함)
            String year = req.getParameter("d_year");
            String month = req.getParameter("d_month");
            String date = req.getParameter("d_date");
            String title = req.getParameter("d_title");
            String txt = req.getParameter("d_txt");
            String visibility = req.getParameter("d_visibility"); // 👈 화면에서 보낸 설정값!

            String id = "pass02"; // 임시 아이디

            // 날짜 합치기
            String formattedMonth = String.format("%02d", Integer.parseInt(month));
            String formattedDay = String.format("%02d", Integer.parseInt(date));
            String fullDate = year + "-" + formattedMonth + "-" + formattedDay;

            pstmt.setString(1, id);
            pstmt.setString(2, fullDate);
            pstmt.setString(3, title);
            pstmt.setString(4, txt);

            // ★ 5번째 물음표에 visibility 설정값 꽂아넣기!
            // 만약 값이 안 넘어왔으면 기본값 2(전체공개)로 세팅
            int visValue = (visibility == null || visibility.equals("")) ? 2 : Integer.parseInt(visibility);
            pstmt.setInt(5, visValue);

            if (pstmt.executeUpdate() == 1) {
                System.out.println("일기 등록 완벽 성공! (공개설정 적용 완료)");
            }

        } catch (Exception e) {
            System.out.println("일기 등록 실패 ㅠㅠ");
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    // 상세보기 기능
    public void getDiaryDetail(HttpServletRequest req) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DBManager.connect();

            // 글 번호와 날짜 챙기기
            String no = req.getParameter("no");
            String y = req.getParameter("y");
            String m = req.getParameter("m");
            String d = req.getParameter("d");

            // 글 검색!
            String sql = "SELECT * FROM diary_test WHERE d_no = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(no));
            rs = pstmt.executeQuery();

            if (rs.next()) {
                DiaryDTO dto = new DiaryDTO();
                dto.setNo(rs.getInt("d_no"));
                dto.setId(rs.getString("d_id"));
                dto.setTitle(rs.getString("d_title"));
                dto.setTxt(rs.getString("d_txt"));
                dto.setDate(rs.getDate("d_date"));

                // ★ DB에서 d_visibility 값을 꺼내서 DTO에 쏙! (수정 화면에 띄우기 위함)
                dto.setVisibility(rs.getInt("d_visibility"));

                req.setAttribute("diary", dto);
            }

            // 뒤로가기용 날짜
            req.setAttribute("curYear", y);
            req.setAttribute("curMonth", m);
            req.setAttribute("selectedDay", d);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, rs);
        }
    }

    // 삭제 기능
    public void deleteDiary(HttpServletRequest request) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBManager.connect();
            String no = request.getParameter("no");
            String sql = "DELETE FROM diary_test WHERE d_no = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(no));

            if (pstmt.executeUpdate() == 1) {
                System.out.println("일기 삭제 완벽 성공!");
            }

        } catch (Exception e) {
            System.out.println("일기 삭제 실패 ㅠㅠ");
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }

    // 수정 기능
    public void updateDiary(HttpServletRequest request) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBManager.connect();

            // 파라미터 받기
            String no = request.getParameter("no");
            String title = request.getParameter("d_title");
            String txt = request.getParameter("d_txt");
            String visibility = request.getParameter("d_visibility"); // 👈 폼에서 변경한 설정값!

            // ★ SQL 수정: d_visibility 컬럼도 덮어쓰도록 쿼리 추가!
            String sql = "UPDATE diary_test SET d_title = ?, d_txt = ?, d_visibility = ? WHERE d_no = ?";
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, title);
            pstmt.setString(2, txt);
            // 만약 값이 안 넘어왔으면 기본값 2로 세팅
            int visValue = (visibility == null || visibility.equals("")) ? 2 : Integer.parseInt(visibility);
            pstmt.setInt(3, visValue);
            pstmt.setInt(4, Integer.parseInt(no));

            if (pstmt.executeUpdate() == 1) {
                System.out.println("일기 수정 완벽 성공! (공개설정 변경 완료)");
            }

        } catch (Exception e) {
            System.out.println("일기 수정 실패 ㅠㅠ");
            e.printStackTrace();
        } finally {
            DBManager.close(con, pstmt, null);
        }
    }
}